package com.aicrm.core.message.application;

import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.dto.MessageResponse;
import com.aicrm.core.ticket.domain.TicketRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetTicketMessagesService {

    private final TicketRepository ticketRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public GetTicketMessagesService(
            TicketRepository ticketRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.ticketRepository = ticketRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long ticketId) {
        ticketRepository.getById(ticketId);
        Conversation conversation = conversationRepository.getByTicketId(ticketId);
        return messageRepository.findAllByConversationIdOrderByCreatedAtAsc(conversation.getId()).stream()
                .map(message -> MessageResponseMapper.toResponse(message, ticketId))
                .toList();
    }
}
