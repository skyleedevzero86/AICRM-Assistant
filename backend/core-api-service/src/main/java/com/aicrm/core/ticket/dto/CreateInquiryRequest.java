package com.aicrm.core.ticket.dto;

import com.aicrm.core.ticket.domain.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateInquiryRequest(
        @NotNull Long customerId,
        @NotNull Long categoryId,
        @NotBlank String subject,
        @NotBlank String content,
        @NotNull ChannelType channel
) {
}
