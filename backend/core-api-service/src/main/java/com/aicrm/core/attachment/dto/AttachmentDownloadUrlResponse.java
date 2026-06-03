package com.aicrm.core.attachment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "첨부파일 다운로드 URL 응답")
public record AttachmentDownloadUrlResponse(
        @Schema(description = "첨부파일 ID", example = "1")
        Long attachmentId,
        @Schema(description = "다운로드 URL", example = "http://localhost:9000/aicrm-attachments/tickets/1/uuid/receipt.pdf?X-Amz-...")
        String downloadUrl,
        @Schema(description = "URL 만료 일시", example = "2025-06-03T12:00:00Z")
        Instant expiresAt
) {
}
