package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.UserRole;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String email,
        UserRole role
) {
}
