package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.UserRole;

public record MeResponse(
        Long userId,
        String email,
        String name,
        UserRole role
) {
}
