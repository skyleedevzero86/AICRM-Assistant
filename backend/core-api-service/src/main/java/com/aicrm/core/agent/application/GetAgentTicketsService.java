package com.aicrm.core.agent.application;

import com.aicrm.core.agent.dto.AgentTicketSummaryResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAgentTicketsService {

    private final TicketRepository ticketRepository;
    private final AgentContext agentContext;

    public GetAgentTicketsService(TicketRepository ticketRepository, AgentContext agentContext) {
        this.ticketRepository = ticketRepository;
        this.agentContext = agentContext;
    }

    @Transactional(readOnly = true)
    public List<AgentTicketSummaryResponse> getMyTickets() {
        Long agentId = agentContext.requireAgentId();
        return ticketRepository.findAllByAgentIdOrderByCreatedAtDesc(agentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AgentTicketSummaryResponse toResponse(Ticket ticket) {
        ConsultationCategory category = ticket.getCategory();
        return new AgentTicketSummaryResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getCustomer().getName(),
                category != null ? category.getName() : "",
                ticket.getChannel(),
                ticket.getCreatedAt(),
                ticket.getClosedAt()
        );
    }
}
