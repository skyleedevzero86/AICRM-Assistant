package com.aicrm.core.message.application;

import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.application.CurrentCustomerService;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.dto.MessageResponse;
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
public class GetTicketMessagesService {

    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CurrentCustomerService currentCustomerService;
    private final AgentContext agentContext;

    public GetTicketMessagesService(
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            CurrentUserProvider currentUserProvider,
            CurrentCustomerService currentCustomerService,
            AgentContext agentContext
    ) {
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.currentUserProvider = currentUserProvider;
        this.currentCustomerService = currentCustomerService;
        this.agentContext = agentContext;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long ticketId) {
        AuthenticatedUser user = currentUserProvider.require();
        if (user.role() == UserRole.CUSTOMER) {
            Long customerId = currentCustomerService.requireCustomerId();
            ticketRepository.getByIdAndCustomerId(ticketId, customerId);
        } else if (user.role() == UserRole.AGENT) {
            Ticket ticket = ticketRepository.getById(ticketId);
            if (ticket.getStatus() != TicketStatus.WAITING
                    && !Objects.equals(ticket.getAgentId(), agentContext.requireAgentId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        } else {
            ticketRepository.getById(ticketId);
        }
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(message -> MessageResponseMapper.toResponse(message, ticketId))
                .toList();
    }
}
