package com.aicrm.core.customer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.category.domain.ConsultationCategoryRepository;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.customer.dto.CreateCustomerInquiryRequest;
import com.aicrm.core.customer.dto.CreateCustomerInquiryResponse;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.ticket.application.TicketNoGenerator;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateCustomerInquiryServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ConsultationCategoryRepository consultationCategoryRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketNoGenerator ticketNoGenerator;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private CreateCustomerInquiryService createCustomerInquiryService;

    private Customer existingCustomer;
    private ConsultationCategory category;
    private CreateCustomerInquiryRequest request;

    @BeforeEach
    void setUp() {
        existingCustomer = customer(1L);
        category = category(3L);
        request = new CreateCustomerInquiryRequest(
                "김고객",
                "010-1234-5678",
                "customer@test.com",
                3L,
                "배송이 늦어요",
                "지난주 주문했는데 아직 도착하지 않았습니다."
        );
    }

    @Test
    void createUsesExistingCustomerWhenPhoneMatches() {
        when(currentUserProvider.require()).thenReturn(new AuthenticatedUser(100L, "customer@test.com", "김고객", UserRole.CUSTOMER));
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.of(existingCustomer));
        when(consultationCategoryRepository.getEnabledLeafCategory(3L)).thenReturn(category);
        when(ticketNoGenerator.generateNext()).thenReturn("TICKET-20260530-0001");
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            return savedTicket(ticket, 10L);
        });
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conversation = invocation.getArgument(0);
            return savedConversation(conversation, 20L);
        });
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CreateCustomerInquiryResponse response = createCustomerInquiryService.create(request);

        // then
        assertThat(response.ticketId()).isEqualTo(10L);
        assertThat(response.ticketNo()).isEqualTo("TICKET-20260530-0001");
        assertThat(response.status()).isEqualTo(TicketStatus.WAITING);
        verify(customerRepository, never()).save(any(Customer.class));

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());
        assertThat(messageCaptor.getAllValues()).hasSize(1);
    }

    @Test
    void createCreatesNewCustomerWhenPhoneNotFound() {
        when(currentUserProvider.require()).thenReturn(new AuthenticatedUser(100L, "customer@test.com", "김고객", UserRole.CUSTOMER));
        when(customerRepository.findByUserId(100L)).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customer = invocation.getArgument(0);
            return customerWithId(customer, 2L);
        });
        when(consultationCategoryRepository.getEnabledLeafCategory(3L)).thenReturn(category);
        when(ticketNoGenerator.generateNext()).thenReturn("TICKET-20260530-0002");
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            return savedTicket(ticket, 11L);
        });
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(invocation -> {
            Conversation conversation = invocation.getArgument(0);
            return savedConversation(conversation, 21L);
        });
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        CreateCustomerInquiryResponse response = createCustomerInquiryService.create(request);

        // then
        assertThat(response.ticketId()).isEqualTo(11L);
        assertThat(response.ticketNo()).isEqualTo("TICKET-20260530-0002");
        verify(customerRepository).save(any(Customer.class));
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

    private Customer customerWithId(Customer customer, Long id) {
        try {
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

    private Ticket savedTicket(Ticket ticket, Long id) {
        try {
            var idField = Ticket.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(ticket, id);
            return ticket;
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }

    private Conversation savedConversation(Conversation conversation, Long id) {
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
