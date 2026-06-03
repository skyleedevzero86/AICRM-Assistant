package com.aicrm.core.attachment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "bucket_name", nullable = false)
    private String bucketName;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt = Instant.now();

    protected Attachment() {
    }

    public static Attachment create(
            Long ticketId,
            Long messageId,
            String originalFilename,
            String storedFilename,
            String contentType,
            long fileSize,
            String bucketName,
            String objectKey,
            Long uploadedBy
    ) {
        Attachment attachment = new Attachment();
        attachment.ticketId = ticketId;
        attachment.messageId = messageId;
        attachment.originalFilename = originalFilename;
        attachment.storedFilename = storedFilename;
        attachment.contentType = contentType;
        attachment.fileSize = fileSize;
        attachment.bucketName = bucketName;
        attachment.objectKey = objectKey;
        attachment.uploadedBy = uploadedBy;
        attachment.uploadedAt = Instant.now();
        return attachment;
    }

    public Long getId() {
        return id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
