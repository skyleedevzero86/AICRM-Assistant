package com.aicrm.core.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMeRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(min = 8, max = 100) String password,
        @Size(max = 30) String phone
) {
}
