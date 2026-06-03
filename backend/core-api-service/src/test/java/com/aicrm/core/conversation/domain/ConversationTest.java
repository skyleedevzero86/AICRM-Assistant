package com.aicrm.core.conversation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import org.junit.jupiter.api.Test;

class ConversationTest {

    @Test
    void endSetsEndedAtOnce() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(3L), ChannelType.WEB_INQUIRY, "문의", "TICKET-20260601-0001");
        Conversation conversation = Conversation.start(ticket, ChannelType.WEB_INQUIRY);

        // when
        conversation.end();
        var firstEndedAt = conversation.getEndedAt();
        conversation.end();

        // then
        assertThat(firstEndedAt).isNotNull();
        assertThat(conversation.getEndedAt()).isEqualTo(firstEndedAt);
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
