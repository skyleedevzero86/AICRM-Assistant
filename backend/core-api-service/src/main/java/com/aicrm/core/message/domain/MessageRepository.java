package com.aicrm.core.message.domain;

import java.util.Optional;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findFirstCustomerMessageByConversationId(Long conversationId);
}
