package com.aicrm.core.attachment.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.aicrm.core.global.config.AttachmentProperties;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.support.MessagesInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class AttachmentFileValidatorTest {

    private AttachmentFileValidator validator;

    @BeforeEach
    void setUp() {
        MessagesInitializer.init();
        AttachmentProperties properties = new AttachmentProperties();
        properties.setMaxFileSizeBytes(1024);
        properties.setAllowedExtensions("txt,pdf");
        validator = new AttachmentFileValidator(properties);
    }

    @Test
    void validateAcceptsAllowedFile() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.txt",
                "text/plain",
                "hello".getBytes()
        );

        // when & then
        assertDoesNotThrow(() -> validator.validate(file));
    }

    @Test
    void validateRejectsEmptyFile() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "sample.txt", "text/plain", new byte[0]);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> validator.validate(file));
        assertEquals(ErrorCode.FILE_REQUIRED, exception.getErrorCode());
    }

    @Test
    void validateRejectsOversizedFile() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.txt",
                "text/plain",
                new byte[2048]
        );

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> validator.validate(file));
        assertEquals(ErrorCode.FILE_TOO_LARGE, exception.getErrorCode());
    }

    @Test
    void validateRejectsDisallowedExtension() {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "data".getBytes()
        );

        // when & then
        BusinessException exception = assertThrows(BusinessException.class, () -> validator.validate(file));
        assertEquals(ErrorCode.FILE_EXTENSION_NOT_ALLOWED, exception.getErrorCode());
    }
}
