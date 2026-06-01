package com.aicrm.core.agent.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.agent.dto.CloseTicketRequest;
import com.aicrm.core.agent.dto.CloseTicketResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.application.event.TicketClosedEvent;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CloseTicketServiceTest {

    @Mock
    private AgentContext agentContext;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CloseTicketService closeTicketService;

    @Test
    void closeSavesResolutionAndEndsConversation() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);
        Conversation conversation = conversationWithId(ticket, 20L);
        String resolution = "배송 지연 사유를 안내하고 예상 도착일을 전달했습니다.";

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getByIdForUpdate(10L)).thenReturn(ticket);
        when(ticketRepository.save(ticket)).thenReturn(ticket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(conversationRepository.save(conversation)).thenReturn(conversation);

        // when
        CloseTicketResponse response = closeTicketService.close(10L, new CloseTicketRequest(resolution));

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(TicketStatus.CLOSED);
        assertThat(response.resolution()).isEqualTo(resolution);
        assertThat(response.closedAt()).isNotNull();
        assertThat(response.conversationEndedAt()).isNotNull();
        assertThat(ticket.getResolution()).isEqualTo(resolution);
        assertThat(conversation.getEndedAt()).isNotNull();

        ArgumentCaptor<TicketClosedEvent> eventCaptor = ArgumentCaptor.forClass(TicketClosedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        TicketClosedEvent event = eventCaptor.getValue();
        assertThat(event.ticketId()).isEqualTo(10L);
        assertThat(event.agentId()).isEqualTo(1L);
        assertThat(event.conversationId()).isEqualTo(20L);
        assertThat(event.resolution()).isEqualTo(resolution);
    }

    @Test
    void closeRejectsUnassignedAgent() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);

        when(agentContext.requireAgentId()).thenReturn(2L);
        when(ticketRepository.getByIdForUpdate(10L)).thenReturn(ticket);

        // when & then
        assertThatThrownBy(() -> closeTicketService.close(
                10L,
                new CloseTicketRequest("처리 완료")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.NOT_ASSIGNED_AGENT);
    }

    @Test
    void closeRejectsWaitingTicket() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(3L), ChannelType.WEB_INQUIRY, "문의", "TICKET-20260601-0001");
        Ticket lockedTicket = ticketWithId(ticket, 11L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getByIdForUpdate(11L)).thenReturn(lockedTicket);

        // when & then
        assertThatThrownBy(() -> closeTicketService.close(
                11L,
                new CloseTicketRequest("처리 완료")
        ))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.TICKET_CANNOT_CLOSE);
    }

    @Test
    void closeRejectsBlankResolution() {
        // given
        Ticket ticket = inProgressTicketWithId(10L, 1L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getByIdForUpdate(10L)).thenReturn(ticket);

        // when & then
        assertThatThrownBy(() -> closeTicketService.close(10L, new CloseTicketRequest("   ")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.RESOLUTION_REQUIRED);
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

    private Ticket inProgressTicketWithId(Long ticketId, Long agentId) {
        Ticket ticket = Ticket.createWaiting(customer(1L), category(3L), ChannelType.WEB_INQUIRY, "문의", "TICKET-20260601-0002");
        ticket.accept(agentId);
        return ticketWithId(ticket, ticketId);
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
}
