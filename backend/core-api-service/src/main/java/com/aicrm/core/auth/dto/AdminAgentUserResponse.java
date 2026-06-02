package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.AgentGrade;

public record AdminAgentUserResponse(
                Long userId,
                Long agentId,
                String employeeNo,
                String name,
                String email,
                AgentAccountStatus approvalStatus,
                AgentGrade grade,
                String withdrawnYn,
                String suspendedYn) {
}
