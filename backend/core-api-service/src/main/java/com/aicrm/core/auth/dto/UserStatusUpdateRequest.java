package com.aicrm.core.auth.dto;

import jakarta.validation.constraints.Pattern;

public record UserStatusUpdateRequest(
        @Pattern(regexp = "Y|N") String value
) {
}
