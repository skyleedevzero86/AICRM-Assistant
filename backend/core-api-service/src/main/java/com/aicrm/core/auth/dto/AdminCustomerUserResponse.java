package com.aicrm.core.auth.dto;

public record AdminCustomerUserResponse(
        Long userId,
        String name,
        String email,
        String phone,
        String withdrawnYn,
        String suspendedYn
) {
}
