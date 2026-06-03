package com.aicrm.app;

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
class MvpCoreFlowIntegrationTest extends PostgresIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void mvpInquiryToCloseFlowWithAuthMessagesAndAttachments() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);
        String agentToken = login(mockMvc, objectMapper, SEED_AGENT1_EMAIL, SEED_PASSWORD);
        long leafCategoryId = findLeafCategoryIdByCode(mockMvc, objectMapper, bearer(customerToken), "DELIVERY_DELAY");

        // when
        MvcResult inquiryResult = mockMvc.perform(post("/api/customer/inquiries")
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "MVP통합고객",
                                  "phone": "010-5555-0001",
                                  "email": "mvp-flow@test.com",
                                  "categoryId": %d,
                                  "title": "MVP 배송 문의",
                                  "content": "MVP 핵심 흐름 검증 문의입니다."
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
                "mvp-문의.txt",
                "text/plain",
                "MVP 첨부".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/customer/tickets/{ticketId}/attachments", ticketId)
                        .file(customerFile)
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", ticketId).header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "MVP 상담원 답변입니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.senderType").value("AGENT"));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/close", ticketId)
                        .header("Authorization", bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resolution": "MVP 처리 완료"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"));

        // then
        mockMvc.perform(get("/api/customer/tickets/{ticketId}", ticketId).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"));

        mockMvc.perform(get("/api/tickets/{ticketId}/messages", ticketId).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.content == 'MVP 상담원 답변입니다.')]").exists());

        mockMvc.perform(get("/api/tickets/{ticketId}/attachments", ticketId).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }
}
