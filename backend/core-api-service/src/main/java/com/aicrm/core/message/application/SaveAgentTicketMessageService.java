package com.aicrm.core.message.application;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.dto.SaveMessageRequest;
import com.aicrm.core.message.dto.SaveMessageResponse;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveAgentTicketMessageService {

    private static final Set<MessageType> AGENT_MESSAGE_TYPES = Set.of(
            MessageType.TEXT,
            MessageType.INTERNAL_MEMO,
            MessageType.AI_DRAFT
    );

    private final AgentContext agentContext;
    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public SaveAgentTicketMessageService(
            AgentContext agentContext,
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.agentContext = agentContext;
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public SaveMessageResponse save(Long ticketId, SaveMessageRequest request) {
        Long agentId = agentContext.requireAgentId();
        Ticket ticket = ticketRepository.getById(ticketId);
        ensureTicketAcceptsMessage(ticket);
        ensureAssignedAgent(ticket, agentId);
        MessageType messageType = resolveAgentMessageType(request.messageType());
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        Message message = Message.agentMessage(
                conversation,
                agentId,
                request.content(),
                messageType
        );
        Message saved = messageRepository.save(message);
        return MessageResponseMapper.toSaveResponse(saved, ticketId);
    }

    private void ensureTicketAcceptsMessage(Ticket ticket) {
        if (!ticket.getStatus().canAddMessage()) {
            throw new BusinessException(ErrorCode.TICKET_CANNOT_ADD_MESSAGE);
        }
    }

    private void ensureAssignedAgent(Ticket ticket, Long agentId) {
        if (ticket.getAgentId() == null || !Objects.equals(ticket.getAgentId(), agentId)) {
            throw new BusinessException(ErrorCode.NOT_ASSIGNED_AGENT);
        }
    }

    private MessageType resolveAgentMessageType(MessageType messageType) {
        MessageType resolved = messageType == null ? MessageType.TEXT : messageType;
        if (!AGENT_MESSAGE_TYPES.contains(resolved)) {
            throw new BusinessException(ErrorCode.INVALID_MESSAGE_TYPE);
        }
        return resolved;
    }
}
