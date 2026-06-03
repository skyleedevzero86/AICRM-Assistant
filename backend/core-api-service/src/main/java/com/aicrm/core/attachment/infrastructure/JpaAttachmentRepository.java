package com.aicrm.core.attachment.infrastructure;

import com.aicrm.core.attachment.domain.Attachment;
import com.aicrm.core.attachment.domain.AttachmentRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAttachmentRepository implements AttachmentRepository {

    private final AttachmentSpringDataJpaRepository springDataJpaRepository;

    public JpaAttachmentRepository(AttachmentSpringDataJpaRepository springDataJpaRepository) {
        this.springDataJpaRepository = springDataJpaRepository;
    }

    @Override
    public Attachment save(Attachment attachment) {
        return springDataJpaRepository.save(attachment);
    }

    @Override
    public Attachment getById(Long attachmentId) {
        return springDataJpaRepository.findById(attachmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTACHMENT_NOT_FOUND, "ATTACHMENT_NOT_FOUND", attachmentId));
    }

    @Override
    public List<Attachment> findAllByTicketIdOrderByUploadedAtAsc(Long ticketId) {
        return springDataJpaRepository.findAllByTicketIdOrderByUploadedAtAsc(ticketId);
    }
}
