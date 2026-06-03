package com.aicrm.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "고객 회원가입 요청")
public record SignUpRequest(
        @Schema(description = "이름", example = "홍길동")
        @NotBlank String name,
        @Schema(description = "이메일", example = "hong@example.com")
        @NotBlank @Email String email,
        @Schema(description = "비밀번호 (8자 이상)", example = "password1234")
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
