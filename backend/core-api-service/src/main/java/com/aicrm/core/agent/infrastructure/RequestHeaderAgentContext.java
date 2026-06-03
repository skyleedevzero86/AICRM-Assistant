package com.aicrm.core.agent.infrastructure;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.infrastructure.AgentAccountRepository;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class RequestHeaderAgentContext implements AgentContext {

    private final CurrentUserProvider currentUserProvider;
    private final AgentAccountRepository agentAccountRepository;

    public RequestHeaderAgentContext(
            CurrentUserProvider currentUserProvider,
            AgentAccountRepository agentAccountRepository
    ) {
        this.currentUserProvider = currentUserProvider;
        this.agentAccountRepository = agentAccountRepository;
    }

    @Override
    public Long requireAgentId() {
        AuthenticatedUser user = currentUserProvider.require();
        if (user.role() != UserRole.AGENT) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return agentAccountRepository.findByUserId(user.userId())
                .filter(agent -> agent.getStatus() == AgentAccountStatus.ACTIVE)
                .map(agent -> agent.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.AGENT_APPROVAL_REQUIRED));
    }
}
