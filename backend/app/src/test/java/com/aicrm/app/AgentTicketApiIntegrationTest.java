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
class AgentTicketApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void agentTicketAcceptFlow() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, "customer@example.com", "password");
        String agentToken = login(mockMvc, objectMapper, "agent1@aicrm.local", "password");
        String agent2Token = login(mockMvc, objectMapper, "agent2@aicrm.local", "password");

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
                                  "customerName": "통합테스트고객",
                                  "phone": "010-8888-0001",
                                  "email": "integration-agent@test.com",
                                  "categoryId": %d,
                                  "title": "배송 지연 문의",
                                  "content": "주문한 상품이 아직 도착하지 않았습니다."
                                }
                                """.formatted(leafCategoryId)))
                .andExpect(status().isCreated())
                .andReturn();

        long ticketId = objectMapper.readTree(inquiryResult.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("data")
                .path("ticketId")
                .asLong();

        // when & then
        mockMvc.perform(get("/api/agent/tickets/waiting").header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[?(@.ticketId == " + ticketId + ")]").exists());

        mockMvc.perform(get("/api/agent/tickets/{ticketId}", ticketId).header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ticketId").value(ticketId))
                .andExpect(jsonPath("$.data.status").value("WAITING"))
                .andExpect(jsonPath("$.data.inquiryContent").value("주문한 상품이 아직 도착하지 않았습니다."));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", ticketId).header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.agentId").value(1));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", ticketId).header("Authorization", bearer(agent2Token)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("TICKET_ALREADY_ASSIGNED"));

        mockMvc.perform(get("/api/agent/tickets/waiting").header("Authorization", bearer(agentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.ticketId == " + ticketId + ")]").doesNotExist());

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/close", ticketId)
                        .header("Authorization", bearer(agent2Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resolution": "다른 상담원 종료 시도"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("NOT_ASSIGNED_AGENT"));

        mockMvc.perform(post("/api/agent/tickets/{ticketId}/close", ticketId)
                        .header("Authorization", bearer(agentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "resolution": "배송 지연 안내 후 종료"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CLOSED"))
                .andExpect(jsonPath("$.data.resolution").value("배송 지연 안내 후 종료"));
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
