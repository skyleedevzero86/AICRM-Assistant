package com.aicrm.app;

import static org.assertj.core.api.Assertions.assertThat;
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
class ConsultationCategoryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getConsultationCategoryTreeReturnsSevenRootCategories() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/categories/consultation/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(7))
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(result.getResponse().getContentAsString(StandardCharsets.UTF_8)).path("data");
        JsonNode deliveryNode = findNodeByCode(rootNodes, "DELIVERY");

        assertThat(deliveryNode.path("name").asText()).isEqualTo("배송");
        assertThat(deliveryNode.path("depth").asInt()).isEqualTo(1);
        assertThat(deliveryNode.path("children").isArray()).isTrue();
        assertThat(deliveryNode.path("children").size()).isGreaterThan(0);

        JsonNode deliveryStatusNode = findNodeByCode(deliveryNode.path("children"), "DELIVERY_STATUS");
        assertThat(deliveryStatusNode.path("name").asText()).isEqualTo("배송 상태");
        assertThat(deliveryStatusNode.path("depth").asInt()).isEqualTo(2);

        JsonNode deliveryDelayNode = findNodeByCode(deliveryStatusNode.path("children"), "DELIVERY_DELAY");
        assertThat(deliveryDelayNode.path("name").asText()).isEqualTo("배송 지연");
        assertThat(deliveryDelayNode.path("depth").asInt()).isEqualTo(3);
        assertThat(deliveryDelayNode.path("children").size()).isEqualTo(0);
    }

    @Test
    void createInquiryAcceptsDepthThreeCategory() throws Exception {
        MvcResult treeResult = mockMvc.perform(get("/api/categories/consultation/tree"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(treeResult.getResponse().getContentAsString(StandardCharsets.UTF_8)).path("data");
        JsonNode deliveryDelayNode = findNodeByCode(rootNodes, "DELIVERY_DELAY");
        long leafCategoryId = deliveryDelayNode.path("id").asLong();

        mockMvc.perform(post("/api/tickets/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "categoryId": %d,
                                  "subject": "Delivery delayed",
                                  "content": "My package has not arrived.",
                                  "channel": "WEB_INQUIRY"
                                }
                                """.formatted(leafCategoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.categoryId").value((int) leafCategoryId))
                .andExpect(jsonPath("$.data.categoryCode").value("DELIVERY_DELAY"))
                .andExpect(jsonPath("$.data.status").value("WAITING"));
    }

    @Test
    void createInquiryRejectsDepthOneCategory() throws Exception {
        MvcResult treeResult = mockMvc.perform(get("/api/categories/consultation/tree"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(treeResult.getResponse().getContentAsString(StandardCharsets.UTF_8)).path("data");
        JsonNode deliveryNode = findNodeByCode(rootNodes, "DELIVERY");
        long rootCategoryId = deliveryNode.path("id").asLong();

        mockMvc.perform(post("/api/tickets/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "categoryId": %d,
                                  "subject": "Invalid category",
                                  "content": "This should fail.",
                                  "channel": "WEB_INQUIRY"
                                }
                                """.formatted(rootCategoryId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CATEGORY_DEPTH"));
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
