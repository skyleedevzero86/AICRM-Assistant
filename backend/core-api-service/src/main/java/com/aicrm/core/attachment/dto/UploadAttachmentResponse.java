package com.aicrm.core.attachment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "첨부파일 업로드 응답")
public record UploadAttachmentResponse(
        @Schema(description = "첨부파일 ID", example = "1")
        Long attachmentId,
        @Schema(description = "티켓 ID", example = "1")
        Long ticketId,
        @Schema(description = "메시지 ID", example = "5")
        Long messageId,
        @Schema(description = "원본 파일명", example = "receipt.pdf")
        String originalFilename,
        @Schema(description = "콘텐츠 타입", example = "application/pdf")
        String contentType,
        @Schema(description = "파일 크기 (바이트)", example = "102400")
        long fileSize,
        @Schema(description = "업로드 일시", example = "2025-06-03T11:00:00Z")
        Instant uploadedAt
) {
}
