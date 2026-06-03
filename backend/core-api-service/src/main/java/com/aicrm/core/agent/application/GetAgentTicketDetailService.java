package com.aicrm.core.agent.application;

import com.aicrm.core.agent.dto.AgentTicketDetailResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAgentTicketDetailService {

    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AgentContext agentContext;

    public GetAgentTicketDetailService(
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            AgentContext agentContext
    ) {
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.agentContext = agentContext;
    }

    @Transactional(readOnly = true)
    public AgentTicketDetailResponse getDetail(Long ticketId) {
        Ticket ticket = ticketRepository.getById(ticketId);
        Long agentId = agentContext.requireAgentId();
        if (ticket.getStatus() != TicketStatus.WAITING && !Objects.equals(ticket.getAgentId(), agentId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        String inquiryContent = messageRepository.findFirstCustomerMessageByConversationId(conversation.getId())
                .map(Message::getContent)
                .orElse(null);

        Customer customer = ticket.getCustomer();
        ConsultationCategory category = ticket.getCategory();
        return new AgentTicketDetailResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getChannel(),
                ticket.getCreatedAt(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                category != null ? category.getId() : null,
                category != null ? category.getName() : null,
                inquiryContent,
                ticket.getAgentId(),
                ticket.getResolution(),
                ticket.getClosedAt()
        );
    }
}
