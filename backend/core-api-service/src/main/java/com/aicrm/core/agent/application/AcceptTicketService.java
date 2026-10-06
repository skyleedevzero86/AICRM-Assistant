package com.aicrm.core.agent.application;

import com.aicrm.core.agent.dto.AcceptTicketResponse;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcceptTicketService {

    private final AgentContext agentContext;
    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;

    public AcceptTicketService(
            AgentContext agentContext,
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository
    ) {
        this.agentContext = agentContext;
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    public AcceptTicketResponse accept(Long ticketId) {
        Long agentId = agentContext.requireAgentId();
        Ticket ticket = ticketRepository.getByIdForUpdate(ticketId);
        ticket.accept(agentId);
        ticketRepository.save(ticket);

        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        conversation.assignAgent(agentId);
        conversationRepository.save(conversation);

        return new AcceptTicketResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getAgentId()
        );
    }
}
