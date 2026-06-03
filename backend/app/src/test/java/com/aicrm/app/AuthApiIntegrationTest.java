package com.aicrm.app;

import static com.aicrm.app.support.IntegrationKoreanErrors.AUTH_FAILED;
import static com.aicrm.app.support.IntegrationKoreanErrors.FORBIDDEN;
import static com.aicrm.app.support.IntegrationKoreanErrors.UNAUTHORIZED;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_AGENT1_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_CUSTOMER_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_PASSWORD;
import static com.aicrm.app.support.IntegrationTestSupport.bearer;
import static com.aicrm.app.support.IntegrationTestSupport.login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.app.support.PostgresIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthApiIntegrationTest extends PostgresIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    void loginReturnsJwtForCustomerAndAgent() throws Exception {
        // given
        String customerEmail = SEED_CUSTOMER_EMAIL;
        String agentEmail = SEED_AGENT1_EMAIL;
        String password = SEED_PASSWORD;

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(customerEmail, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(agentEmail, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("AGENT"));
    }

    @Test
    void loginFailsWithKoreanMessageWhenPasswordInvalid() throws Exception {
        // given
        String email = SEED_CUSTOMER_EMAIL;

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "wrong-password"
                                }
                                """.formatted(email)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("AUTH_FAILED"))
                .andExpect(jsonPath("$.error.message").value(AUTH_FAILED));
    }

    @Test
    void protectedApiRequiresJwt() throws Exception {
        // when & then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.error.message").value(UNAUTHORIZED));

        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer("invalid.jwt.token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.error.message").value(UNAUTHORIZED));
    }

    @Test
    void roleAccessControlBlocksCrossRoleEndpoints() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);
        String agentToken = login(mockMvc, objectMapper, SEED_AGENT1_EMAIL, SEED_PASSWORD);

        // when & then
        mockMvc.perform(get("/api/customer/tickets").header("Authorization", bearer(agentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.error.message").value(FORBIDDEN));

        mockMvc.perform(get("/api/agent/tickets/waiting").header("Authorization", bearer(customerToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.error.message").value(FORBIDDEN));
    }

    @Test
    void meReturnsAuthenticatedUser() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);

        // when & then
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(SEED_CUSTOMER_EMAIL))
                .andExpect(jsonPath("$.data.role").value("CUSTOMER"));
    }
}
