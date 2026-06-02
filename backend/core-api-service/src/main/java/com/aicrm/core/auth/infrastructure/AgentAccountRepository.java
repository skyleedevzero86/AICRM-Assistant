package com.aicrm.core.auth.infrastructure;

import com.aicrm.core.auth.domain.AgentAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentAccountRepository extends JpaRepository<AgentAccount, Long> {

    Optional<AgentAccount> findByUserId(Long userId);
}
