package com.aicrm.core.message.infrastructure;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
import com.aicrm.core.message.domain.SenderType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMessageRepository implements MessageRepository {

    private final MessageSpringDataJpaRepository springDataJpaRepository;

    public JpaMessageRepository(MessageSpringDataJpaRepository springDataJpaRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
    }

    @Override
    public Message save(Message message) {
        return springDataJpaRepository.save(message);
    }

    @Override
    public Optional<Message> findFirstCustomerMessageByConversationId(Long conversationId) {
        return springDataJpaRepository.findFirstByConversation_IdAndSenderTypeOrderByCreatedAtAsc(
                conversationId,
                SenderType.CUSTOMER
        );
    }

    @Override
    public List<Message> findAllByConversationIdOrderByCreatedAtAsc(Long conversationId) {
        return springDataJpaRepository.findAllByConversation_IdOrderByCreatedAtAsc(conversationId);
    }

    @Override
    public Message getById(Long messageId) {
        return springDataJpaRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND, "MESSAGE_NOT_FOUND", messageId));
    }
}
