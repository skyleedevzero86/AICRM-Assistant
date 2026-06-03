package com.aicrm.core.ticket.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.support.MessagesInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TicketTest {

    @BeforeAll
    static void setUpMessages() {
        MessagesInitializer.init();
    }

    @Test
    void createWaitingInitializesWaitingTicket() {
        // given
        Customer customer = customer(1L);
        ConsultationCategory category = category(10L);

        // when
        Ticket ticket = Ticket.createWaiting(customer, category, ChannelType.WEB_INQUIRY, "로그인 문의", "TICKET-20260531-0001");

        // then
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.WAITING);
        assertThat(ticket.getCustomer()).isEqualTo(customer);
        assertThat(ticket.getCategory()).isEqualTo(category);
        assertThat(ticket.getChannel()).isEqualTo(ChannelType.WEB_INQUIRY);
        assertThat(ticket.getSubject()).isEqualTo("로그인 문의");
        assertThat(ticket.getTicketNo()).isEqualTo("TICKET-20260531-0001");
    }

    @Test
    void acceptChangesStatusToInProgress() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260531-0002");

        // when
        ticket.accept(100L);

        // then
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(ticket.getAgentId()).isEqualTo(100L);
    }

    @Test
    void acceptRejectsAlreadyAssignedTicket() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260531-0004");
        ticket.accept(100L);

        // when & then
        assertThatThrownBy(() -> ticket.accept(200L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_ALREADY_ASSIGNED);
    }

    @Test
    void closeStoresResolutionAndClosedAt() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260601-0001");
        ticket.accept(100L);
        String resolution = "배송 지연 사유를 안내하고 예상 도착일을 전달했습니다.";

        // when
        ticket.close(100L, resolution);

        // then
        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CLOSED);
        assertThat(ticket.getResolution()).isEqualTo(resolution);
        assertThat(ticket.getClosedAt()).isNotNull();
    }

    @Test
    void closeRejectsOtherAgent() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260601-0002");
        ticket.accept(100L);

        // when & then
        assertThatThrownBy(() -> ticket.close(200L, "처리 완료"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_ASSIGNED_AGENT);
    }

    @Test
    void closeRejectsWaitingTicket() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260601-0003");

        // when & then
        assertThatThrownBy(() -> ticket.close(100L, "처리 완료"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_CANNOT_CLOSE);
    }

    @Test
    void closeRejectsBlankResolution() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260601-0004");
        ticket.accept(100L);

        // when & then
        assertThatThrownBy(() -> ticket.close(100L, "  "))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOLUTION_REQUIRED);
    }

    @Test
    void acceptRejectsClosedTicket() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(10L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260531-0003");
        ticket.accept(100L);
        ticket.close(100L, "처리 완료");

        // when & then
        assertThatThrownBy(() -> ticket.accept(200L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_ALREADY_ASSIGNED);
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
