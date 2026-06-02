package com.aicrm.core.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgentSignUpRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank
        @Pattern(regexp = "^\\d{16}$", message = "사원번호는 16자리 숫자(연월일시분초+순번)여야 합니다")
        String employeeNo
) {
}
