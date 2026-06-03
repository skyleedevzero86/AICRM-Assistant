package com.aicrm.core.attachment.application;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.attachment.domain.AttachmentRepository;
import com.aicrm.core.attachment.dto.AttachmentResponse;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.customer.application.CurrentCustomerService;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTicketAttachmentsService {

    private final AttachmentRepository attachmentRepository;
    private final TicketRepository ticketRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CurrentCustomerService currentCustomerService;
    private final AgentContext agentContext;

    public GetTicketAttachmentsService(
            AttachmentRepository attachmentRepository,
            TicketRepository ticketRepository,
            CurrentUserProvider currentUserProvider,
            CurrentCustomerService currentCustomerService,
            AgentContext agentContext
    ) {
        this.attachmentRepository = attachmentRepository;
        this.ticketRepository = ticketRepository;
        this.currentUserProvider = currentUserProvider;
        this.currentCustomerService = currentCustomerService;
        this.agentContext = agentContext;
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponse> getAttachments(Long ticketId) {
        ensureTicketAccess(ticketId);
        return attachmentRepository.findAllByTicketIdOrderByUploadedAtAsc(ticketId).stream()
                .map(AttachmentResponseMapper::toResponse)
                .toList();
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
