package com.aicrm.core.conversation.infrastructure;

import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaConversationRepository implements ConversationRepository {

    private final ConversationSpringDataJpaRepository springDataJpaRepository;

    public JpaConversationRepository(ConversationSpringDataJpaRepository springDataJpaRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
    }

    @Override
    public Conversation save(Conversation conversation) {
        return springDataJpaRepository.save(conversation);
    }
}
