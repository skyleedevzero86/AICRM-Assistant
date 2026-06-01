package com.aicrm.core.agent.application;

import com.aicrm.core.agent.dto.WaitingTicketResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetWaitingTicketsService {

    private final TicketRepository ticketRepository;

    public GetWaitingTicketsService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Transactional(readOnly = true)
    public List<WaitingTicketResponse> getWaitingTickets() {
        return ticketRepository.findAllByStatusOrderByCreatedAtAsc(TicketStatus.WAITING).stream()
                .map(this::toResponse)
                .toList();
    }

    private WaitingTicketResponse toResponse(Ticket ticket) {
        Customer customer = ticket.getCustomer();
        ConsultationCategory category = ticket.getCategory();
        return new WaitingTicketResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getSubject(),
                customer.getName(),
                category != null ? category.getName() : null,
                ticket.getChannel(),
                ticket.getCreatedAt()
        );
    }
}
