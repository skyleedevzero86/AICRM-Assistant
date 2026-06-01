package com.aicrm.core.conversation.infrastructure;

import com.aicrm.core.conversation.domain.Conversation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationSpringDataJpaRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByTicket_Id(Long ticketId);
}
