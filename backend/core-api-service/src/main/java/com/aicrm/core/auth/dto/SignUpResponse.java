package com.aicrm.core.auth.dto;

import com.aicrm.core.auth.domain.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답")
public record SignUpResponse(
        @Schema(description = "사용자 ID", example = "3")
        Long userId,
        @Schema(description = "이메일", example = "hong@example.com")
        String email,
        @Schema(description = "역할", example = "CUSTOMER")
        UserRole role,
        @Schema(description = "가입 상태", example = "ACTIVE")
        String signupStatus
) {
}
