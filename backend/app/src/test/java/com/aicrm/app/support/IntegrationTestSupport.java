package com.aicrm.app.support;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public final class IntegrationTestSupport {

    public static final String SEED_CUSTOMER_EMAIL = "customer@example.com";
    public static final String SEED_AGENT1_EMAIL = "agent1@aicrm.local";
    public static final String SEED_AGENT2_EMAIL = "agent2@aicrm.local";
    public static final String SEED_PASSWORD = "password";

    private IntegrationTestSupport() {
    }

    public static String login(MockMvc mockMvc, ObjectMapper objectMapper, String email, String password) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode data = objectMapper.readTree(loginResult.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("data");
        return data.path("accessToken").asText();
    }

    public static String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }

    public static long findLeafCategoryIdByCode(MockMvc mockMvc, ObjectMapper objectMapper, String bearerToken, String code)
            throws Exception {
        MvcResult treeResult = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/categories/consultation/tree")
                                .header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode rootNodes = objectMapper.readTree(treeResult.getResponse().getContentAsString(StandardCharsets.UTF_8))
                .path("data");
        JsonNode node = findNodeByCode(rootNodes, code);
        if (node == null || node.isMissingNode()) {
            throw new IllegalStateException("카테고리 코드를 찾을 수 없습니다: " + code);
        }
        return node.path("id").asLong();
    }

    public static JsonNode findNodeByCode(JsonNode nodes, String code) {
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
