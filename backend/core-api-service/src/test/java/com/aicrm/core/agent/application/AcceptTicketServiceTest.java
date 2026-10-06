package com.aicrm.core.agent.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.agent.dto.AcceptTicketResponse;
import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcceptTicketServiceTest {

    @Mock
    private AgentContext agentContext;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConversationRepository conversationRepository;

    @InjectMocks
    private AcceptTicketService acceptTicketService;

    @Test
    void acceptAssignsAgentAndUpdatesConversation() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(3L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260531-0001");
        Ticket lockedTicket = ticketWithId(ticket, 10L);
        Conversation conversation = conversationForTicket(lockedTicket, 20L);

        when(agentContext.requireAgentId()).thenReturn(1L);
        when(ticketRepository.getByIdForUpdate(10L)).thenReturn(lockedTicket);
        when(ticketRepository.save(lockedTicket)).thenReturn(lockedTicket);
        when(conversationRepository.getByTicketId(10L)).thenReturn(conversation);
        when(conversationRepository.save(conversation)).thenReturn(conversation);

        // when
        AcceptTicketResponse response = acceptTicketService.accept(10L);

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.ticketNo()).isEqualTo("TICKET-20260531-0001");
        assertThat(response.status()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(response.agentId()).isEqualTo(1L);
        assertThat(conversation.getAgentId()).isEqualTo(1L);
        verify(ticketRepository).getByIdForUpdate(10L);
    }

    @Test
    void acceptRejectsAlreadyAssignedTicket() {
        // given
        Ticket ticket = Ticket.createWaiting(customer(1L), category(3L), ChannelType.WEB_INQUIRY, "문의 제목", "TICKET-20260531-0002");
        ticket.accept(1L);
        Ticket lockedTicket = ticketWithId(ticket, 11L);

        when(agentContext.requireAgentId()).thenReturn(2L);
        when(ticketRepository.getByIdForUpdate(11L)).thenReturn(lockedTicket);

        // when & then
        assertThatThrownBy(() -> acceptTicketService.accept(11L))
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
}
