package com.aicrm.core.message.infrastructure;

import com.aicrm.core.message.domain.Message;
import com.aicrm.core.message.domain.MessageRepository;
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
}
