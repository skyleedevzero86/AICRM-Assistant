package com.aicrm.core.message.application;

import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.customer.application.CurrentCustomerService;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.dto.SaveMessageRequest;
import com.aicrm.core.message.dto.SaveMessageResponse;
import com.aicrm.core.ticket.domain.Ticket;
import com.aicrm.core.ticket.domain.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveCustomerTicketMessageService {

    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CurrentCustomerService currentCustomerService;

    public SaveCustomerTicketMessageService(
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            CurrentCustomerService currentCustomerService
    ) {
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.currentCustomerService = currentCustomerService;
    }

    @Transactional
    public SaveMessageResponse save(Long ticketId, SaveMessageRequest request) {
        Long customerId = currentCustomerService.requireCustomerId();
        Ticket ticket = ticketRepository.getByIdAndCustomerId(ticketId, customerId);
        ensureTicketAcceptsMessage(ticket);
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        Message message = Message.customerText(
                conversation,
                ticket.getCustomer().getId(),
                request.content()
        );
        Message saved = messageRepository.save(message);
        return MessageResponseMapper.toSaveResponse(saved, ticketId);
    }

    private void ensureTicketAcceptsMessage(Ticket ticket) {
        if (!ticket.getStatus().canAddMessage()) {
            throw new BusinessException(ErrorCode.TICKET_CANNOT_ADD_MESSAGE);
        }
    }
}
