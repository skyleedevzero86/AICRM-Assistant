package com.aicrm.core.attachment.application;

import com.aicrm.core.attachment.domain.Attachment;
import com.aicrm.core.attachment.dto.AttachmentResponse;
import com.aicrm.core.attachment.dto.UploadAttachmentResponse;

final class AttachmentResponseMapper {

    private AttachmentResponseMapper() {
    }

    static UploadAttachmentResponse toUploadResponse(Attachment attachment) {
        return new UploadAttachmentResponse(
                attachment.getId(),
                attachment.getTicketId(),
                attachment.getMessageId(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getUploadedAt()
        );
    }

    static AttachmentResponse toResponse(Attachment attachment) {
        return new AttachmentResponse(
                attachment.getId(),
                attachment.getTicketId(),
                attachment.getMessageId(),
                attachment.getOriginalFilename(),
                attachment.getContentType(),
                attachment.getFileSize(),
                attachment.getUploadedAt()
        );
    }
}
