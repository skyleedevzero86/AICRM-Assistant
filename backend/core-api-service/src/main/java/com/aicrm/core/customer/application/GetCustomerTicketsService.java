package com.aicrm.core.customer.application;

import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.message.application.GetTicketMessagesService;
import com.aicrm.core.message.domain.SenderType;
import com.aicrm.core.message.dto.MessageResponse;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerTicketsService {

    private final TicketRepository ticketRepository;
    private final GetTicketMessagesService getTicketMessagesService;

    public GetCustomerTicketsService(
            TicketRepository ticketRepository,
            GetTicketMessagesService getTicketMessagesService
    ) {
        this.ticketRepository = ticketRepository;
        this.getTicketMessagesService = getTicketMessagesService;
    }

    @Transactional(readOnly = true)
    public List<CustomerTicketSummaryResponse> getTickets() {
        return ticketRepository.findAllOrderByCreatedAtDesc().stream()
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerTicketDetailResponse getTicket(Long ticketId) {
        Ticket ticket = ticketRepository.getById(ticketId);
        String inquiryContent = getTicketMessagesService.getMessages(ticketId).stream()
                .filter(message -> message.senderType() == SenderType.CUSTOMER)
                .min(Comparator.comparing(MessageResponse::createdAt))
                .map(MessageResponse::content)
                .orElse("");

        return new CustomerTicketDetailResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getCategory().getId(),
                ticket.getCategory().getName(),
                ticket.getCreatedAt(),
                ticket.getCustomer().getName(),
                ticket.getCustomer().getPhone(),
                ticket.getCustomer().getEmail(),
                inquiryContent,
                ticket.getResolution(),
                ticket.getClosedAt()
        );
    }

    private CustomerTicketSummaryResponse toSummary(Ticket ticket) {
        return new CustomerTicketSummaryResponse(
                ticket.getId(),
                ticket.getTicketNo(),
                ticket.getStatus(),
                ticket.getSubject(),
                ticket.getCategory().getName(),
                ticket.getCreatedAt()
        );
    }
}
