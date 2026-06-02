package com.aicrm.core.auth.security;

import com.aicrm.core.auth.domain.UserRole;

public record AuthenticatedUser(
        Long userId,
        String email,
        String name,
        UserRole role
) {
}
