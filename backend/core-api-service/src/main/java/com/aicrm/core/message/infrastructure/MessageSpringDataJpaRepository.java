package com.aicrm.core.message.infrastructure;

import com.aicrm.core.message.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSpringDataJpaRepository extends JpaRepository<Message, Long> {
}
