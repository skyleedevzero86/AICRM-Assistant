package com.aicrm.app;

import static com.aicrm.app.IntegrationTestAuth.bearer;
import static com.aicrm.app.IntegrationTestAuth.login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
class CustomerTicketApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Test
    void customerTicketAccessIsScopedToLoggedInCustomer() throws Exception {
        // given
        String customerToken = login(mockMvc, objectMapper, "customer@example.com", "password");

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
                .andExpect(jsonPath("$.error.code").value("TICKET_NOT_FOUND"));

        mockMvc.perform(get("/api/tickets/{ticketId}/messages", 2L).header("Authorization", bearer(customerToken)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TICKET_NOT_FOUND"));

        String agentToken = login(mockMvc, objectMapper, "agent1@aicrm.local", "password");
        mockMvc.perform(get("/api/customer/tickets").header("Authorization", bearer(agentToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }
}
