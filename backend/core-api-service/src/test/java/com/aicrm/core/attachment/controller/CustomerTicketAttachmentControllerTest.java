package com.aicrm.core.attachment.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.attachment.application.UploadCustomerTicketAttachmentService;
import com.aicrm.core.attachment.dto.UploadAttachmentResponse;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.support.MessagesInitializer;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CustomerTicketAttachmentControllerTest {

    @Mock
    private UploadCustomerTicketAttachmentService uploadCustomerTicketAttachmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MessagesInitializer.init();
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerTicketAttachmentController(uploadCustomerTicketAttachmentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void uploadReturnsCreatedAttachment() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "문의.txt", "text/plain", "문의 첨부".getBytes());
        when(uploadCustomerTicketAttachmentService.upload(eq(10L), eq(file), isNull()))
                .thenReturn(new UploadAttachmentResponse(
                        1L,
                        10L,
                        null,
                        "문의.txt",
                        "text/plain",
                        10L,
                        Instant.parse("2026-06-03T10:00:00Z")
                ));

        // when & then
        mockMvc.perform(multipart("/api/customer/tickets/{ticketId}/attachments", 10L).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.attachmentId").value(1))
                .andExpect(jsonPath("$.data.originalFilename").value("문의.txt"));

        verify(uploadCustomerTicketAttachmentService).upload(eq(10L), eq(file), isNull());
    }

    @Test
    void uploadReturnsBadRequestWhenExtensionNotAllowed() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "script.exe", "application/octet-stream", "bad".getBytes());
        when(uploadCustomerTicketAttachmentService.upload(eq(10L), eq(file), isNull()))
                .thenThrow(new BusinessException(ErrorCode.FILE_EXTENSION_NOT_ALLOWED));

        // when & then
        mockMvc.perform(multipart("/api/customer/tickets/{ticketId}/attachments", 10L).file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("FILE_EXTENSION_NOT_ALLOWED"));
    }
}
