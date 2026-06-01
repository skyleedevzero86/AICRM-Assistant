package com.aicrm.core.agent.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.aicrm.core.agent.dto.WaitingTicketResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWaitingTicketsServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private GetWaitingTicketsService getWaitingTicketsService;

    @Test
    void getWaitingTicketsReturnsOnlyWaitingTickets() {
        // given
        Ticket waitingTicket = waitingTicket(1L, "TICKET-20260531-0001", "로그인 문의");
        when(ticketRepository.findAllByStatusOrderByCreatedAtAsc(TicketStatus.WAITING))
                .thenReturn(List.of(waitingTicket));

        // when
        List<WaitingTicketResponse> responses = getWaitingTicketsService.getWaitingTickets();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).ticketId()).isEqualTo(1L);
        assertThat(responses.get(0).ticketNo()).isEqualTo("TICKET-20260531-0001");
        assertThat(responses.get(0).customerName()).isEqualTo("김고객");
        assertThat(responses.get(0).categoryName()).isEqualTo("일반 문의");
    }

    private Ticket waitingTicket(Long id, String ticketNo, String subject) {
        Ticket ticket = Ticket.createWaiting(
                customer(1L, "김고객"),
                category(1L, "일반 문의"),
                ChannelType.WEB_INQUIRY,
                subject,
                ticketNo
        );
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, id);
            var createdAtField = Ticket.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(ticket, Instant.parse("2026-05-31T10:00:00Z"));
            return ticket;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Customer customer(Long id, String name) {
        try {
            var constructor = Customer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Customer customer = constructor.newInstance();
            var idField = Customer.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(customer, id);
            var nameField = Customer.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(customer, name);
            return customer;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private ConsultationCategory category(Long id, String name) {
        try {
            var constructor = ConsultationCategory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            ConsultationCategory category = constructor.newInstance();
            var idField = ConsultationCategory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, id);
            var nameField = ConsultationCategory.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(category, name);
            return category;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
