package com.aicrm.core.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCustomerInquiryRequest(
        @NotBlank String customerName,
        @NotBlank @Email String email,
        @NotNull Long categoryId,
        @NotBlank String title,
        @NotBlank String content
) {
}
