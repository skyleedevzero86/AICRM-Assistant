package com.aicrm.app;

import static com.aicrm.app.support.IntegrationKoreanErrors.FORBIDDEN;
import static com.aicrm.app.support.IntegrationKoreanErrors.TICKET_NOT_FOUND;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_AGENT1_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_CUSTOMER_EMAIL;
import static com.aicrm.app.support.IntegrationTestSupport.SEED_PASSWORD;
import static com.aicrm.app.support.IntegrationTestSupport.bearer;
import static com.aicrm.app.support.IntegrationTestSupport.login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.app.support.PostgresIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerTicketApiIntegrationTest extends PostgresIntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    void customerTicketAccessIsScopedToLoggedInCustomer() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, SEED_CUSTOMER_EMAIL, SEED_PASSWORD);

        // when & then
        mockMvc.perform(get("/api/customer/tickets").header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[?(@.ticketId == 1)]").exists())
                .andExpect(jsonPath("$.data[?(@.ticketId == 3)]").exists())
                .andExpect(jsonPath("$.data[?(@.ticketId == 2)]").doesNotExist());

        mockMvc.perform(get("/api/customer/tickets/{ticketId}", 1L).header("Authorization", bearer(customerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ticketId").value(1));

        mockMvc.perform(get("/api/customer/tickets/{ticketId}", 2L).header("Authorization", bearer(customerToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TICKET_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value(org.hamcrest.Matchers.containsString(TICKET_NOT_FOUND)));

        mockMvc.perform(get("/api/tickets/{ticketId}/messages", 2L).header("Authorization", bearer(customerToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TICKET_NOT_FOUND"));

        String agentToken = login(mockMvc, objectMapper, SEED_AGENT1_EMAIL, SEED_PASSWORD);
        mockMvc.perform(get("/api/customer/tickets").header("Authorization", bearer(agentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"))
                .andExpect(jsonPath("$.error.message").value(FORBIDDEN));
    }
}
