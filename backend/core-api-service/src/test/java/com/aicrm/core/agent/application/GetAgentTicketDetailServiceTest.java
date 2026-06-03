package com.aicrm.core.agent.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.aicrm.core.agent.dto.AgentTicketDetailResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAgentTicketDetailServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AgentContext agentContext;

    @InjectMocks
    private GetAgentTicketDetailService getAgentTicketDetailService;

    @Test
    void getDetailAllowsWaitingTicketForAnyAgent() {
        // given
        Ticket ticket = waitingTicketWithId(10L);
        Conversation conversation = conversationForTicket(ticket, 20L);
        Message inquiry = customerMessage("문의 내용");

        when(agentContext.requireAgentId()).thenReturn(2L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.findFirstCustomerMessageByConversationId(20L)).thenReturn(Optional.of(inquiry));

        // when
        AgentTicketDetailResponse response = getAgentTicketDetailService.getDetail(10L);

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.inquiryContent()).isEqualTo("문의 내용");
    }

    @Test
    void getDetailAllowsInProgressTicketForAssignedAgent() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);
        Conversation conversation = conversationForTicket(ticket, 20L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(messageRepository.findFirstCustomerMessageByConversationId(20L)).thenReturn(Optional.empty());

        // when
        AgentTicketDetailResponse response = getAgentTicketDetailService.getDetail(10L);

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.agentId()).isEqualTo(1L);
    }

    @Test
    void getDetailRejectsInProgressTicketForOtherAgent() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);

        when(agentContext.requireAgentId()).thenReturn(2L);
        when(ticketRepository.getById(10L)).thenReturn(ticket);

        // when & then
        assertThatThrownBy(() -> getAgentTicketDetailService.getDetail(10L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    private Ticket waitingTicketWithId(Long id) {
        return ticketWithId(
                Ticket.createWaiting(customer(1L), category(3L), ChannelType.WEB_INQUIRY, "문의", "TICKET-20260601-0001"),
                id
        );
    }

    private Ticket inProgressTicketWithId(Long id, Long agentId) {
        Ticket ticket = waitingTicketWithId(id);
        ticket.accept(agentId);
        return ticket;
    }

    private Ticket ticketWithId(Ticket ticket, Long id) {
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, id);
            return ticket;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Conversation conversationForTicket(Ticket ticket, Long id) {
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

    private Message customerMessage(String content) {
        try {
            var constructor = Message.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Message message = constructor.newInstance();
            var contentField = Message.class.getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(message, content);
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
