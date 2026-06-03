package com.aicrm.core.attachment.application;

import com.aicrm.core.global.config.AttachmentProperties;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AttachmentFileValidator {

    private final AttachmentProperties attachmentProperties;

    public AttachmentFileValidator(AttachmentProperties attachmentProperties) {
        this.attachmentProperties = attachmentProperties;
    }

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_REQUIRED);
        }
        if (file.getSize() > attachmentProperties.getMaxFileSizeBytes()) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
        }
        String extension = extractExtension(file.getOriginalFilename());
        if (!StringUtils.hasText(extension) || !attachmentProperties.allowedExtensionSet().contains(extension)) {
            throw new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED);
        }
    }

    public String resolveContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (StringUtils.hasText(contentType)) {
            return contentType;
        }
        return "application/octet-stream";
    }

    public String sanitizeOriginalFilename(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "file";
        }
        return originalFilename.replace("\\", "/").substring(originalFilename.lastIndexOf('/') + 1);
    }

    public String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        String filename = sanitizeOriginalFilename(originalFilename);
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }
}
