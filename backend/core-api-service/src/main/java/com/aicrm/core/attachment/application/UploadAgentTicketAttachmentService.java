package com.aicrm.core.attachment.application;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.attachment.domain.Attachment;
import com.aicrm.core.attachment.domain.AttachmentRepository;
import com.aicrm.core.attachment.domain.ObjectStorage;
import com.aicrm.core.attachment.dto.UploadAttachmentResponse;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.global.config.MinioProperties;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadAgentTicketAttachmentService {

    private final AgentContext agentContext;
    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentFileValidator attachmentFileValidator;
    private final ObjectStorage objectStorage;
    private final MinioProperties minioProperties;

    public UploadAgentTicketAttachmentService(
            AgentContext agentContext,
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            AttachmentRepository attachmentRepository,
            AttachmentFileValidator attachmentFileValidator,
            ObjectStorage objectStorage,
            MinioProperties minioProperties
    ) {
        this.agentContext = agentContext;
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentFileValidator = attachmentFileValidator;
        this.objectStorage = objectStorage;
        this.minioProperties = minioProperties;
    }

    @Transactional
    public UploadAttachmentResponse upload(Long ticketId, MultipartFile file, Long messageId) {
        Long agentId = agentContext.requireAgentId();
        Ticket ticket = ticketRepository.getById(ticketId);
        ensureTicketAcceptsAttachment(ticket);
        ensureAssignedAgent(ticket, agentId);
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        Long resolvedMessageId = resolveMessageId(conversation, messageId);
        return storeAttachment(ticketId, resolvedMessageId, agentId, file);
    }

    private void ensureTicketAcceptsAttachment(Ticket ticket) {
        if (!ticket.getStatus().canAddMessage()) {
            throw new BusinessException(ErrorCode.TICKET_CANNOT_ADD_MESSAGE);
        }
    }

    private void ensureAssignedAgent(Ticket ticket, Long agentId) {
        if (ticket.getAgentId() == null || !Objects.equals(ticket.getAgentId(), agentId)) {
            throw new BusinessException(ErrorCode.NOT_ASSIGNED_AGENT);
        }
    }

    private Long resolveMessageId(Conversation conversation, Long messageId) {
        if (messageId == null) {
            return null;
        }
        Message message = messageRepository.getById(messageId);
        if (!Objects.equals(message.getConversation().getId(), conversation.getId())) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND, "MESSAGE_NOT_FOUND", messageId);
        }
        return messageId;
    }

    private UploadAttachmentResponse storeAttachment(
            Long ticketId,
            Long messageId,
            Long uploadedBy,
            MultipartFile file
    ) {
        attachmentFileValidator.validate(file);
        String originalFilename = attachmentFileValidator.sanitizeOriginalFilename(file.getOriginalFilename());
        String extension = attachmentFileValidator.extractExtension(originalFilename);
        String storedFilename = UUID.randomUUID() + "." + extension;
        String objectKey = "tickets/" + ticketId + "/" + storedFilename;
        String bucket = minioProperties.getBucket();
        try {
            objectStorage.upload(
                    bucket,
                    objectKey,
                    file.getInputStream(),
                    file.getSize(),
                    attachmentFileValidator.resolveContentType(file)
            );
        } catch (IOException exception) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        Attachment attachment = Attachment.create(
                ticketId,
                messageId,
                originalFilename,
                storedFilename,
                attachmentFileValidator.resolveContentType(file),
                file.getSize(),
                bucket,
                objectKey,
                uploadedBy
        );
        Attachment saved = attachmentRepository.save(attachment);
        return AttachmentResponseMapper.toUploadResponse(saved);
    }
}
