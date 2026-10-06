package com.aicrm.core.customer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.application.GetTicketMessagesService;
import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.domain.SenderType;
import com.aicrm.core.message.dto.MessageResponse;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetCustomerTicketsServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private GetTicketMessagesService getTicketMessagesService;

    @Mock
    private CurrentCustomerService currentCustomerService;

    @InjectMocks
    private GetCustomerTicketsService getCustomerTicketsService;

    @Test
    void getTicketsReturnsOnlyCurrentCustomerTickets() {
        // given
        when(currentCustomerService.resolveCustomerId()).thenReturn(Optional.of(1L));
        Ticket ownedTicket = ticketWithId(10L, 1L);
        when(ticketRepository.findAllByCustomerIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(ownedTicket));

        // when
        List<CustomerTicketSummaryResponse> responses = getCustomerTicketsService.getTickets();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).ticketId()).isEqualTo(10L);
        assertThat(responses.get(0).ticketNo()).isEqualTo("TICKET-20260531-0001");
        verify(ticketRepository).findAllByCustomerIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getTicketsReturnsEmptyListWhenCustomerProfileMissing() {
        // given
        when(currentCustomerService.resolveCustomerId()).thenReturn(Optional.empty());

        // when
        List<CustomerTicketSummaryResponse> responses = getCustomerTicketsService.getTickets();

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    void getTicketReturnsOwnedTicketDetail() {
        // given
        when(currentCustomerService.requireCustomerId()).thenReturn(1L);
        Ticket ticket = ticketWithId(10L, 1L);
        when(ticketRepository.getByIdAndCustomerId(10L, 1L)).thenReturn(ticket);
        when(getTicketMessagesService.getMessages(10L)).thenReturn(List.of(
                new MessageResponse(
                        1L,
                        10L,
                        SenderType.CUSTOMER,
                        1L,
                        MessageType.TEXT,
                        "문의 내용",
                        Instant.parse("2026-05-31T10:00:00Z")
                )
        ));

        // when
        CustomerTicketDetailResponse response = getCustomerTicketsService.getTicket(10L);

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.inquiryContent()).isEqualTo("문의 내용");
        verify(ticketRepository).getByIdAndCustomerId(10L, 1L);
    }

    @Test
    void getTicketRejectsOtherCustomerTicket() {
        // given
        when(currentCustomerService.requireCustomerId()).thenReturn(1L);
        when(ticketRepository.getByIdAndCustomerId(99L, 1L))
                .thenThrow(new BusinessException(ErrorCode.TICKET_NOT_FOUND, "TICKET_NOT_FOUND", 99L));

        // when & then
        assertThatThrownBy(() -> getCustomerTicketsService.getTicket(99L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_NOT_FOUND);
    }

    private Ticket ticketWithId(Long ticketId, Long customerId) {
        Ticket ticket = Ticket.createWaiting(
                customer(customerId),
                category(3L),
                ChannelType.WEB_INQUIRY,
                "배송 문의",
                "TICKET-20260531-0001"
        );
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, ticketId);
            return ticket;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Customer customer(Long id) {
        try {
            var constructor = Customer.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Customer customer = constructor.newInstance();
            var idField = Customer.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(customer, id);
            var nameField = Customer.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(customer, "김고객");
            var phoneField = Customer.class.getDeclaredField("phone");
            phoneField.setAccessible(true);
            phoneField.set(customer, "010-1234-5678");
            var emailField = Customer.class.getDeclaredField("email");
            emailField.setAccessible(true);
            emailField.set(customer, "customer@test.com");
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
            var nameField = ConsultationCategory.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(category, "배송");
            return category;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
