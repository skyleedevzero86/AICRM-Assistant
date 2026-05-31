package com.aicrm.core.ticket.application;

import com.aicrm.core.category.domain.ConsultationCategory;
import com.aicrm.core.category.domain.ConsultationCategoryRepository;
import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import com.aicrm.core.ticket.dto.CreateInquiryRequest;
import com.aicrm.core.ticket.dto.CreateInquiryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerInquiryService {

    private final CustomerRepository customerRepository;
    private final ConsultationCategoryRepository consultationCategoryRepository;
    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public CreateCustomerInquiryService(
            CustomerRepository customerRepository,
            ConsultationCategoryRepository consultationCategoryRepository,
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.customerRepository = customerRepository;
        this.consultationCategoryRepository = consultationCategoryRepository;
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public CreateInquiryResponse create(CreateInquiryRequest request) {
        Customer customer = customerRepository.getById(request.customerId());
        ConsultationCategory category = consultationCategoryRepository.getEnabledLeafCategory(request.categoryId());

        Ticket ticket = Ticket.createWaiting(
                customer,
                category,
                request.channel(),
                request.subject()
        );
        Ticket savedTicket = ticketRepository.save(ticket);

        Conversation conversation = Conversation.start(savedTicket, request.channel());
        Conversation savedConversation = conversationRepository.save(conversation);

        Message customerMessage = Message.customerText(savedConversation, customer.getId(), request.content());
        Message savedCustomerMessage = messageRepository.save(customerMessage);

        messageRepository.save(Message.systemNotice(
                savedConversation,
                "Your inquiry has been received. An agent will respond shortly."
        ));

        return new CreateInquiryResponse(
                savedTicket.getId(),
                savedConversation.getId(),
                savedCustomerMessage.getId(),
                category.getId(),
                category.getCode(),
                category.getName(),
                savedTicket.getStatus(),
                savedTicket.getChannel(),
                savedTicket.getSubject(),
                savedTicket.getCreatedAt()
        );
    }
}
