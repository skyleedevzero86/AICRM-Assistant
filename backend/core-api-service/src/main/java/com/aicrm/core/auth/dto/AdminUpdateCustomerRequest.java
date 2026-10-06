package com.aicrm.core.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUpdateCustomerRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email String email,
        @Size(max = 30) String phone
) {
}
