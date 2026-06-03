package com.aicrm.app;

import static com.aicrm.app.support.IntegrationKoreanErrors.INVALID_CATEGORY_DEPTH;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_CUSTOMER_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_PASSWORD;
import static com.aicrm.app.support.IntegrationTestSupport.bearer;
import static com.aicrm.app.support.IntegrationTestSupport.findNodeByCode;
import static com.aicrm.app.support.IntegrationTestSupport.login;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.app.support.PostgresIntegrationTestSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultationCategoryApiIntegrationTest extends PostgresIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getConsultationCategoryTreeReturnsSevenRootCategories() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);

        // when
        MvcResult result = mockMvc.perform(get("/api/categories/consultation/tree")
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(7))
                .andReturn();

        // then
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
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);
        MvcResult treeResult = mockMvc.perform(get("/api/categories/consultation/tree")
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(treeResult.getResponse().getContentAsString(StandardCharsets.UTF_8)).path("data");
        JsonNode deliveryDelayNode = findNodeByCode(rootNodes, "DELIVERY_DELAY");
        long leafCategoryId = deliveryDelayNode.path("id").asLong();

        // when & then
        mockMvc.perform(post("/api/customer/inquiries")
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "김고객",
                                  "phone": "010-9999-0001",
                                  "email": "newcustomer@test.com",
                                  "categoryId": %d,
                                  "title": "배송이 늦어요",
                                  "content": "지난주 주문했는데 아직 도착하지 않았습니다."
                                }
                                """.formatted(leafCategoryId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticketId").exists())
                .andExpect(jsonPath("$.data.ticketNo").value(org.hamcrest.Matchers.matchesPattern("TICKET-\\d{8}-\\d{4}")))
                .andExpect(jsonPath("$.data.status").value("WAITING"));
    }

    @Test
    void createInquiryRejectsDepthOneCategory() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);
        MvcResult treeResult = mockMvc.perform(get("/api/categories/consultation/tree")
                        .header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(treeResult.getResponse().getContentAsString(StandardCharsets.UTF_8)).path("data");
        JsonNode deliveryNode = findNodeByCode(rootNodes, "DELIVERY");
        long rootCategoryId = deliveryNode.path("id").asLong();

        // when & then
        mockMvc.perform(post("/api/customer/inquiries")
                        .header("Authorization", bearer(customerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "김고객",
                                  "phone": "010-9999-0002",
                                  "email": "customer@test.com",
                                  "categoryId": %d,
                                  "title": "잘못된 카테고리",
                                  "content": "요청이 실패해야 합니다."
                                }
                                """.formatted(rootCategoryId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_CATEGORY_DEPTH"))
                .andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString(INVALID_CATEGORY_DEPTH)));
    }
}
