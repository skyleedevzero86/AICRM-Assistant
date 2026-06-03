package com.aicrm.core.agent.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "티켓 종료 요청")
public record CloseTicketRequest(
        @Schema(description = "해결 내용", example = "환불 처리가 완료되었습니다.")
        @NotBlank(message = "해결 내용은 필수입니다")
        String resolution
) {
}
