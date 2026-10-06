package com.aicrm.core.message.infrastructure;

import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.SenderType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSpringDataJpaRepository extends JpaRepository<Message, Long> {

    Optional<Message> findFirstByConversation_IdAndSenderTypeOrderByCreatedAtAsc(
            Long conversationId,
            SenderType senderType
    );

    List<Message> findAllByConversation_IdOrderByCreatedAtAsc(Long conversationId);
}
