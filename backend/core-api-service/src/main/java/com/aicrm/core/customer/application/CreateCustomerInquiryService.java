package com.aicrm.core.customer.application;

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
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerInquiryService {

    private final CustomerRepository customerRepository;
    private final ConsultationCategoryRepository consultationCategoryRepository;
    private final TicketRepository ticketRepository;
    private final TicketNoGenerator ticketNoGenerator;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CurrentUserProvider currentUserProvider;

    public CreateCustomerInquiryService(
            CustomerRepository customerRepository,
            ConsultationCategoryRepository consultationCategoryRepository,
            TicketRepository ticketRepository,
            TicketNoGenerator ticketNoGenerator,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.customerRepository = customerRepository;
        this.consultationCategoryRepository = consultationCategoryRepository;
        this.ticketRepository = ticketRepository;
        this.ticketNoGenerator = ticketNoGenerator;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional
    public CreateCustomerInquiryResponse create(CreateCustomerInquiryRequest request) {
        Customer customer = resolveCustomer(request);
        ConsultationCategory category = consultationCategoryRepository.getEnabledLeafCategory(request.categoryId());
        String ticketNo = ticketNoGenerator.generateNext();

        Ticket ticket = Ticket.createWaiting(
                customer,
                category,
                ChannelType.WEB_INQUIRY,
                request.title(),
                ticketNo
        );
        Ticket savedTicket = ticketRepository.save(ticket);

        Conversation conversation = Conversation.start(savedTicket, ChannelType.WEB_INQUIRY);
        Conversation savedConversation = conversationRepository.save(conversation);

        messageRepository.save(Message.customerText(savedConversation, customer.getId(), request.content()));

        return new CreateCustomerInquiryResponse(
                savedTicket.getId(),
                savedTicket.getTicketNo(),
                savedTicket.getStatus()
        );
    }

    private Customer resolveCustomer(CreateCustomerInquiryRequest request) {
        AuthenticatedUser user = currentUserProvider.require();
        if (user.role() != UserRole.CUSTOMER) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return customerRepository.findByUserId(user.userId())
                .orElseGet(() -> customerRepository.save(Customer.create(
                        request.customerName(),
                        request.phone(),
                        request.email(),
                        user.userId()
                )));
    }
}
