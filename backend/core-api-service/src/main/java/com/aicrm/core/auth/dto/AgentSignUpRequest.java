package com.aicrm.core.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "상담원 회원가입 요청")
public record AgentSignUpRequest(
        @Schema(description = "이름", example = "김상담")
        @NotBlank String name,
        @Schema(description = "이메일", example = "agent@example.com")
        @NotBlank @Email String email,
        @Schema(description = "비밀번호 (8자 이상)", example = "password1234")
        @NotBlank @Size(min = 8, max = 100) String password,
        @Schema(description = "사번 (16자리 숫자)", example = "1234567890123456")
        @NotBlank
        @Pattern(regexp = "^\\d{16}$", message = "{validation.agent.employeeNo.pattern}")
        String employeeNo
) {
}
