package com.aicrm.core.message.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaveAgentTicketMessageServiceTest {

    @Mock
    private AgentContext agentContext;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private SaveAgentTicketMessageService saveAgentTicketMessageService;

    @Test
    void savePersistsAgentTextReply() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);
        Conversation conversation = conversationWithId(ticket, 20L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            return messageWithId(message, 200L);
        });

        // when
        SaveMessageResponse response = saveAgentTicketMessageService.save(
                10L,
                new SaveMessageRequest("확인 후 안내드리겠습니다.", null)
        );

        // then
        assertThat(response.senderType()).isEqualTo(SenderType.AGENT);
        assertThat(response.messageType()).isEqualTo(MessageType.TEXT);
        assertThat(response.content()).isEqualTo("확인 후 안내드리겠습니다.");
    }

    @Test
    void savePersistsInternalMemoWithAgentSender() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);
        Conversation conversation = conversationWithId(ticket, 20L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SaveMessageResponse response = saveAgentTicketMessageService.save(
                10L,
                new SaveMessageRequest("내부 확인 필요", MessageType.INTERNAL_MEMO)
        );

        // then
        assertThat(response.senderType()).isEqualTo(SenderType.AGENT);
        assertThat(response.messageType()).isEqualTo(MessageType.INTERNAL_MEMO);
    }

    @Test
    void savePersistsAiDraftWithAiSender() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);
        Conversation conversation = conversationWithId(ticket, 20L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SaveMessageResponse response = saveAgentTicketMessageService.save(
                10L,
                new SaveMessageRequest("AI 초안 답변", MessageType.AI_DRAFT)
        );

        // then
        assertThat(response.senderType()).isEqualTo(SenderType.AI);
        assertThat(response.messageType()).isEqualTo(MessageType.AI_DRAFT);
    }

    @Test
    void saveRejectsUnassignedAgent() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);
        when(agentContext.requireAgentId()).thenReturn(2L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);

        // when & then
        assertThatThrownBy(() -> saveAgentTicketMessageService.save(
                10L,
                new SaveMessageRequest("답변", null)
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_ASSIGNED_AGENT);
    }

    private Ticket inProgressTicketWithId(Long id, Long agentId) {
        Ticket ticket = Ticket.createWaiting(
                customer(1L),
                category(3L),
                ChannelType.WEB_INQUIRY,
                "문의",
                "TICKET-20260531-0001"
        );
        ticket.accept(agentId);
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
