package com.aicrm.core.attachment.dto;

import java.time.Instant;

public record AttachmentDownloadUrlResponse(
        Long attachmentId,
        String downloadUrl,
        Instant expiresAt
) {
}
