package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.AgentGrade;
import jakarta.validation.constraints.NotNull;

public record AgentGradeUpdateRequest(
        @NotNull AgentGrade grade
) {
}
