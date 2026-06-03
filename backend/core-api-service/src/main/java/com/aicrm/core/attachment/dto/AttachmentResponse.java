package com.aicrm.core.attachment.dto;

import java.time.Instant;

public record AttachmentResponse(
        Long attachmentId,
        Long ticketId,
        Long messageId,
        String originalFilename,
        String contentType,
        long fileSize,
        Instant uploadedAt
) {
}
