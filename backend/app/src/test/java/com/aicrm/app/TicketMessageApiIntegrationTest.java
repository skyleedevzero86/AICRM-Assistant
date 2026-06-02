package com.aicrm.app;

import static com.aicrm.app.IntegrationTestAuth.bearer;
import static com.aicrm.app.IntegrationTestAuth.login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TicketMessageApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void ticketMessageFlow() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, "customer@example.com", "password");
        String agentToken = login(mockMvc, objectMapper, "agent1@aicrm.local", "password");

        MvcResult treeResult = mockMvc.perform(get("/api/categories/consultation/tree"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(treeResult.getResponse().getContentAsString(StandardCharsets.UTF_8)).path("data");
        long leafCategoryId = findNodeByCode(rootNodes, "DELIVERY_DELAY").path("id").asLong();

        MvcResult inquiryResult = mockMvc.perform(post("/api/customer/inquiries")
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "메시지통합고객",
                                  "phone": "010-7777-0001",
                                  "email": "message-integration@test.com",
                                  "categoryId": %d,
                                  "title": "배송 문의",
                                  "content": "배송이 지연되고 있습니다."
                                }
                                """.formatted(leafCategoryId)))
                .andExpect(status().isCreated())
                .andReturn();

        long ticketId = objectMapper.readTree(inquiryResult.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("data")
                .path("ticketId")
                .asLong();

        // when & then
        mockMvc.perform(get("/api/tickets/{ticketId}/messages", ticketId).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].senderType").value("CUSTOMER"))
                .andExpect(jsonPath("$.data[0].content").value("배송이 지연되고 있습니다."));

        mockMvc.perform(post("/api/customer/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "추가로 송장 번호를 알려드리겠습니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.senderType").value("CUSTOMER"));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "답변 시도"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("NOT_ASSIGNED_AGENT"));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", ticketId).header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/messages", ticketId)
                        .header("Authorization", bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "확인 후 안내드리겠습니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.senderType").value("AGENT"))
                .andExpect(jsonPath("$.data.messageType").value("TEXT"));

        mockMvc.perform(get("/api/tickets/{ticketId}/messages", ticketId).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].senderType").value("CUSTOMER"))
                .andExpect(jsonPath("$.data[1].senderType").value("CUSTOMER"))
                .andExpect(jsonPath("$.data[2].senderType").value("AGENT"))
                .andExpect(jsonPath("$.data[2].content").value("확인 후 안내드리겠습니다."));
    }

    private JsonNode findNodeByCode(JsonNode nodes, String code) {
        for (JsonNode node : nodes) {
            if (code.equals(node.path("code").asText())) {
                return node;
            }
            JsonNode childMatch = findNodeByCode(node.path("children"), code);
            if (childMatch != null) {
                return childMatch;
            }
        }
        return null;
    }
}
