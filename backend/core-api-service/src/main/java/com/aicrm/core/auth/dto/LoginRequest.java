package com.aicrm.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "이메일", example = "customer@example.com")
        @NotBlank @Email String email,
        @Schema(description = "비밀번호", example = "password")
        @NotBlank String password
) {
}
