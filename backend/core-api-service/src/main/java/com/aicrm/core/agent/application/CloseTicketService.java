package com.aicrm.core.agent.application;

import com.aicrm.core.agent.dto.CloseTicketRequest;
import com.aicrm.core.agent.dto.CloseTicketResponse;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.ticket.application.event.TicketClosedEvent;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CloseTicketService {

    private final AgentContext agentContext;
    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CloseTicketService(
            AgentContext agentContext,
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.agentContext = agentContext;
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CloseTicketResponse close(Long ticketId, CloseTicketRequest request) {
        Long agentId = agentContext.requireAgentId();
        Ticket ticket = ticketRepository.getByIdForUpdate(ticketId);
        ticket.close(agentId, request.resolution());
        ticketRepository.save(ticket);

        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        conversation.end();
        conversationRepository.save(conversation);

        publishTicketClosedEvent(ticket, conversation);

        return new CloseTicketResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getResolution(),
                ticket.getClosedAt(),
                conversation.getEndedAt()
        );
    }

    private void publishTicketClosedEvent(Ticket ticket, Conversation conversation) {
        eventPublisher.publishEvent(new TicketClosedEvent(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getAgentId(),
                ticket.getCustomer().getId(),
                conversation.getId(),
                ticket.getResolution(),
                ticket.getClosedAt(),
                conversation.getEndedAt()
        ));
    }
}
