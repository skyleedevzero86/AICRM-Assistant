package com.aicrm.core.conversation.infrastructure;

import com.aicrm.core.conversation.domain.Conversation;
import com.aicrm.core.conversation.domain.ConversationRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
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

    @Override
    public Conversation getByTicketId(Long ticketId) {
        return springDataJpaRepository.findByTicket_Id(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "CONVERSATION_NOT_FOUND", ticketId));
    }
}
