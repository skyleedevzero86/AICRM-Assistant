package com.aicrm.core.agent.infrastructure;

import com.aicrm.core.agent.application.AgentContext;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestHeaderAgentContext implements AgentContext {

    public static final String AGENT_ID_HEADER = "X-Agent-Id";

    @Override
    public Long requireAgentId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ErrorCode.AGENT_ID_REQUIRED);
        }

        String headerValue = attributes.getRequest().getHeader(AGENT_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) {
            throw new BusinessException(ErrorCode.AGENT_ID_REQUIRED);
        }

        try {
            return Long.parseLong(headerValue.trim());
        } catch (NumberFormatException exception) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "X-Agent-Id 헤더 값이 올바르지 않습니다");
        }
    }
}
