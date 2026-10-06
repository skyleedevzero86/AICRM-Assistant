package com.aicrm.core.message.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.application.CurrentCustomerService;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.domain.SenderType;
import com.aicrm.core.message.dto.SaveMessageRequest;
import com.aicrm.core.message.dto.SaveMessageResponse;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveCustomerTicketMessageServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private CurrentCustomerService currentCustomerService;

    @InjectMocks
    private SaveCustomerTicketMessageService saveCustomerTicketMessageService;

    @Test
    void savePersistsCustomerTextMessage() {
        // given
        Ticket ticket = ticketWithId(10L);
        Conversation conversation = conversationWithId(ticket, 20L);
        SaveMessageRequest request = new SaveMessageRequest("추가 문의 내용입니다.", null);

        when(currentCustomerService.requireCustomerId()).thenReturn(1L);
        when(ticketRepository.getByIdAndCustomerId(10L, 1L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            return messageWithId(message, 100L);
        });

        // when
        SaveMessageResponse response = saveCustomerTicketMessageService.save(10L, request);

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.senderType()).isEqualTo(SenderType.CUSTOMER);
        assertThat(response.messageType()).isEqualTo(MessageType.TEXT);
        assertThat(response.content()).isEqualTo("추가 문의 내용입니다.");

        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(captor.capture());
        assertThat(captor.getValue().getSenderType()).isEqualTo(SenderType.CUSTOMER);
        assertThat(captor.getValue().getMessageType()).isEqualTo(MessageType.TEXT);
    }

    @Test
    void saveRejectsOtherCustomerTicket() {
        // given
        when(currentCustomerService.requireCustomerId()).thenReturn(1L);
        when(ticketRepository.getByIdAndCustomerId(10L, 1L))
                .thenThrow(new BusinessException(ErrorCode.TICKET_NOT_FOUND, "TICKET_NOT_FOUND", 10L));

        // when & then
        assertThatThrownBy(() -> saveCustomerTicketMessageService.save(
                10L,
                new SaveMessageRequest("추가 문의", null)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_NOT_FOUND);
    }

    @Test
    void saveRejectsClosedTicket() {
        // given
        Ticket ticket = closedTicketWithId(10L);
        when(currentCustomerService.requireCustomerId()).thenReturn(1L);
        when(ticketRepository.getByIdAndCustomerId(10L, 1L)).thenReturn(ticket);

        // when & then
        assertThatThrownBy(() -> saveCustomerTicketMessageService.save(
                10L,
                new SaveMessageRequest("추가 문의", null)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_CANNOT_ADD_MESSAGE);
    }

    private Ticket ticketWithId(Long id) {
        Ticket ticket = Ticket.createWaiting(
                customer(1L),
                category(3L),
                ChannelType.WEB_INQUIRY,
                "문의",
                "TICKET-20260531-0001"
        );
        return assignId(ticket, id);
    }

    private Ticket closedTicketWithId(Long id) {
        Ticket ticket = ticketWithId(id);
        ticket.accept(1L);
        ticket.close(1L, "처리 완료");
        return ticket;
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

    private Message messageWithId(Message message, Long id) {
        try {
            var idField = Message.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(message, id);
            return message;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Ticket assignId(Ticket ticket, Long id) {
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, id);
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
