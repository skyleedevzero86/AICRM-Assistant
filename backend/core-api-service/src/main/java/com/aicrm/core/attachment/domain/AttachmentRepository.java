package com.aicrm.core.attachment.domain;

import java.util.List;

public interface AttachmentRepository {

    Attachment save(Attachment attachment);

    Attachment getById(Long attachmentId);

    List<Attachment> findAllByTicketIdOrderByUploadedAtAsc(Long ticketId);
}
