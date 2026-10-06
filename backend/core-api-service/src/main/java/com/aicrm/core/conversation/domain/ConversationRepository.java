package com.aicrm.core.conversation.domain;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Conversation getByTicketId(Long ticketId);
}
