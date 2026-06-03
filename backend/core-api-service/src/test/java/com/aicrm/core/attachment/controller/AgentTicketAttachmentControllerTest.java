package com.aicrm.core.attachment.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.attachment.application.UploadAgentTicketAttachmentService;
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
class AgentTicketAttachmentControllerTest {

    @Mock
    private UploadAgentTicketAttachmentService uploadAgentTicketAttachmentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MessagesInitializer.init();
        mockMvc = MockMvcBuilders.standaloneSetup(new AgentTicketAttachmentController(uploadAgentTicketAttachmentService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void uploadReturnsCreatedAttachment() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "답변.pdf", "application/pdf", "pdf".getBytes());
        when(uploadAgentTicketAttachmentService.upload(eq(20L), eq(file), isNull()))
                .thenReturn(new UploadAttachmentResponse(
                        2L,
                        20L,
                        null,
                        "답변.pdf",
                        "application/pdf",
                        3L,
                        Instant.parse("2026-06-03T11:00:00Z")
                ));

        // when & then
        mockMvc.perform(multipart("/api/agent/tickets/{ticketId}/attachments", 20L).file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.attachmentId").value(2))
                .andExpect(jsonPath("$.data.originalFilename").value("답변.pdf"));

        verify(uploadAgentTicketAttachmentService).upload(eq(20L), eq(file), isNull());
    }

    @Test
    void uploadReturnsForbiddenWhenAgentNotAssigned() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "답변.txt", "text/plain", "text".getBytes());
        when(uploadAgentTicketAttachmentService.upload(eq(20L), eq(file), isNull()))
                .thenThrow(new BusinessException(ErrorCode.NOT_ASSIGNED_AGENT));

        // when & then
        mockMvc.perform(multipart("/api/agent/tickets/{ticketId}/attachments", 20L).file(file))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("NOT_ASSIGNED_AGENT"));
    }
}
