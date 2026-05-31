package com.aicrm.core.conversation.infrastructure;

import com.aicrm.core.conversation.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationSpringDataJpaRepository extends JpaRepository<Conversation, Long> {
}
