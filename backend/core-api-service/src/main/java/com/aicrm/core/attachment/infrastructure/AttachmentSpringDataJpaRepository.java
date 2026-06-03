package com.aicrm.core.attachment.infrastructure;

import com.aicrm.core.attachment.domain.Attachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentSpringDataJpaRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findAllByTicketIdOrderByUploadedAtAsc(Long ticketId);
}
