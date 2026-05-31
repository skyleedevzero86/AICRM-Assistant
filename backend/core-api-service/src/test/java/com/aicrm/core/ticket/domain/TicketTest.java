package com.aicrm.core.ticket.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;

class TicketTest {

    @Test
    void createWaitingInitializesWaitingTicket() {
        Customer customer = customer(1L);
        ConsultationCategory category = category(10L);

        Ticket ticket = Ticket.createWaiting(customer, category, ChannelType.WEB_INQUIRY, "Login issue", "TICKET-20260531-0001");

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.WAITING);
        assertThat(ticket.getCustomer()).isEqualTo(customer);
        assertThat(ticket.getCategory()).isEqualTo(category);
        assertThat(ticket.getChannel()).isEqualTo(ChannelType.WEB_INQUIRY);
        assertThat(ticket.getSubject()).isEqualTo("Login issue");
        assertThat(ticket.getTicketNo()).isEqualTo("TICKET-20260531-0001");
    }

    @Test
    void acceptChangesStatusToInProgress() {
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "Issue", "TICKET-20260531-0002");

        ticket.accept(100L);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getAgentId()).isEqualTo(100L);
    }

    @Test
    void acceptRejectsClosedTicket() {
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "Issue", "TICKET-20260531-0003");
        ticket.accept(100L);
        ticket.close(100L, "resolved");

        assertThatThrownBy(() -> ticket.accept(200L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_CANNOT_ACCEPT);
    }

    private Customer customer(Long id) {
        try {
            var constructor = Customer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Customer customer = constructor.newInstance();
            var idField = Customer.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(customer, id);
            return customer;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private ConsultationCategory category(Long id) {
        try {
            var constructor = ConsultationCategory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            ConsultationCategory category = constructor.newInstance();
            var idField = ConsultationCategory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(category, id);
            return category;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
