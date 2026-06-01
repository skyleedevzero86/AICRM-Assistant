package com.aicrm.core.message.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.dto.MessageResponse;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetTicketMessagesServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private GetTicketMessagesService getTicketMessagesService;

    @Test
    void getMessagesReturnsMessagesSortedByCreatedAt() {
        // given
        Ticket ticket = ticketWithId(10L);
        Conversation conversation = conversationWithId(ticket, 20L);
        Message first = messageWithId(
                Message.customerText(conversation, 1L, "첫 문의"),
                1L,
                Instant.parse("2026-05-31T10:00:00Z")
        );
        Message second = messageWithId(
                Message.agentMessage(conversation, 1L, "답변", MessageType.TEXT),
                2L,
                Instant.parse("2026-05-31T10:05:00Z")
        );

        when(ticketRepository.getById(10L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.findAllByConversationIdOrderByCreatedAtAsc(20L)).thenReturn(List.of(first, second));

        // when
        List<MessageResponse> responses = getTicketMessagesService.getMessages(10L);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).messageId()).isEqualTo(1L);
        assertThat(responses.get(0).content()).isEqualTo("첫 문의");
        assertThat(responses.get(1).messageId()).isEqualTo(2L);
        assertThat(responses.get(1).content()).isEqualTo("답변");
        verify(messageRepository).findAllByConversationIdOrderByCreatedAtAsc(20L);
    }

    private Ticket ticketWithId(Long id) {
        Ticket ticket = Ticket.createWaiting(
                customer(1L),
                category(3L),
                ChannelType.WEB_INQUIRY,
                "문의",
                "TICKET-20260531-0001"
        );
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, id);
            return ticket;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Conversation conversationWithId(Ticket ticket, Long id) {
        Conversation conversation = Conversation.start(ticket, ChannelType.WEB_INQUIRY);
        try {
            var idField = Conversation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(conversation, id);
            return conversation;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Message messageWithId(Message message, Long id, Instant createdAt) {
        try {
            var idField = Message.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(message, id);
            var createdAtField = Message.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(message, createdAt);
            return message;
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
