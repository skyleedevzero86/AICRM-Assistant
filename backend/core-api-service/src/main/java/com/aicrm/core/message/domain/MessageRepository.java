package com.aicrm.core.message.domain;

import java.util.List;
import java.util.Optional;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findFirstCustomerMessageByConversationId(Long conversationId);

    List<Message> findAllByConversationIdOrderByCreatedAtAsc(Long conversationId);
}
