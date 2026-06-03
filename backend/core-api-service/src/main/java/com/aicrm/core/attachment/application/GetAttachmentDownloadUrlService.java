package com.aicrm.core.attachment.application;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.attachment.domain.Attachment;
import com.aicrm.core.attachment.domain.AttachmentRepository;
import com.aicrm.core.attachment.domain.ObjectStorage;
import com.aicrm.core.attachment.dto.AttachmentDownloadUrlResponse;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.customer.application.CurrentCustomerService;
import com.aicrm.core.global.config.MinioProperties;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAttachmentDownloadUrlService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final ObjectStorage objectStorage;
    private final MinioProperties minioProperties;
    private final CurrentUserProvider currentUserProvider;
    private final CurrentCustomerService currentCustomerService;
    private final AgentContext agentContext;

    public GetAttachmentDownloadUrlService(
            AttachmentRepository attachmentRepository,
            TicketRepository ticketRepository,
            ObjectStorage objectStorage,
            MinioProperties minioProperties,
            CurrentUserProvider currentUserProvider,
            CurrentCustomerService currentCustomerService,
            AgentContext agentContext
    ) {
        this.attachmentRepository = attachmentRepository;
        this.ticketRepository = ticketRepository;
        this.objectStorage = objectStorage;
        this.minioProperties = minioProperties;
        this.currentUserProvider = currentUserProvider;
        this.currentCustomerService = currentCustomerService;
        this.agentContext = agentContext;
    }

    @Transactional(readOnly = true)
    public AttachmentDownloadUrlResponse getDownloadUrl(Long attachmentId) {
        Attachment attachment = attachmentRepository.getById(attachmentId);
        ensureTicketAccess(attachment.getTicketId());
        int expirySeconds = minioProperties.getPresignedUrlExpirySeconds();
        String downloadUrl = objectStorage.createPresignedDownloadUrl(
                attachment.getBucketName(),
                attachment.getObjectKey(),
                expirySeconds
        );
        return new AttachmentDownloadUrlResponse(
                attachment.getId(),
                downloadUrl,
                Instant.now().plusSeconds(expirySeconds)
        );
    }

    private void ensureTicketAccess(Long ticketId) {
        AuthenticatedUser user = currentUserProvider.require();
        if (user.role() == UserRole.CUSTOMER) {
            Long customerId = currentCustomerService.requireCustomerId();
            ticketRepository.getByIdAndCustomerId(ticketId, customerId);
            return;
        }
        if (user.role() == UserRole.AGENT) {
            Ticket ticket = ticketRepository.getById(ticketId);
            if (ticket.getStatus() != TicketStatus.WAITING
                    && !Objects.equals(ticket.getAgentId(), agentContext.requireAgentId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
            return;
        }
        ticketRepository.getById(ticketId);
    }
}
