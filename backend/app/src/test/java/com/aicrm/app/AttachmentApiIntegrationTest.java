package com.aicrm.app;

import static com.aicrm.app.support.IntegrationKoreanErrors.FILE_EXTENSION_NOT_ALLOWED;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_AGENT1_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_CUSTOMER_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_PASSWORD;
import static com.aicrm.app.support.IntegrationTestSupport.bearer;
import static com.aicrm.app.support.IntegrationTestSupport.findLeafCategoryIdByCode;
import static com.aicrm.app.support.IntegrationTestSupport.login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.app.support.PostgresIntegrationTestSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AttachmentApiIntegrationTest extends PostgresIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void customerAndAgentAttachmentFlow() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);
        String agentToken = login(mockMvc, objectMapper, SEED_AGENT1_EMAIL, SEED_PASSWORD);
        long leafCategoryId = findLeafCategoryIdByCode(mockMvc, objectMapper, bearer(customerToken), "DELIVERY_DELAY");

        MvcResult inquiryResult = mockMvc.perform(post("/api/customer/inquiries")
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "첨부테스트고객",
                                  "phone": "010-8888-0001",
                                  "email": "attachment-integration@test.com",
                                  "categoryId": %d,
                                  "title": "첨부파일 문의",
                                  "content": "첨부파일 테스트 문의입니다."
                                }
                                """.formatted(leafCategoryId)))
                .andExpect(status().isCreated())
                .andReturn();

        long ticketId = objectMapper.readTree(inquiryResult.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("data")
                .path("ticketId")
                .asLong();

        MockMultipartFile customerFile = new MockMultipartFile(
                "file",
                "문의첨부.txt",
                "text/plain",
                "고객 첨부 내용".getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        MvcResult customerUploadResult = mockMvc.perform(multipart("/api/customer/tickets/{ticketId}/attachments", ticketId)
                        .file(customerFile)
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.originalFilename").value("문의첨부.txt"))
                .andReturn();

        long attachmentId = objectMapper.readTree(customerUploadResult.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("data")
                .path("attachmentId")
                .asLong();

        mockMvc.perform(get("/api/tickets/{ticketId}/attachments", ticketId).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].attachmentId").value(attachmentId));

        mockMvc.perform(get("/api/attachments/{attachmentId}/download-url", attachmentId)
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.downloadUrl").isNotEmpty());

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", ticketId).header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk());

        MockMultipartFile agentFile = new MockMultipartFile(
                "file",
                "상담답변.txt",
                "text/plain",
                "상담원 첨부".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/agent/tickets/{ticketId}/attachments", ticketId)
                        .file(agentFile)
                        .header("Authorization", bearer(agentToken)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.originalFilename").value("상담답변.txt"));

        mockMvc.perform(get("/api/tickets/{ticketId}/attachments", ticketId).header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));

        MockMultipartFile invalidFile = new MockMultipartFile(
                "file",
                "실행파일.exe",
                "application/octet-stream",
                "bad".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/customer/tickets/{ticketId}/attachments", ticketId)
                        .file(invalidFile)
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("FILE_EXTENSION_NOT_ALLOWED"))
                .andExpect(jsonPath("$.error.message").value(FILE_EXTENSION_NOT_ALLOWED));
    }
}
