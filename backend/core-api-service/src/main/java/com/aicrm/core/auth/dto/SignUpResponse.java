package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.UserRole;

public record SignUpResponse(
        Long userId,
        String email,
        UserRole role,
        String signupStatus
) {
}
