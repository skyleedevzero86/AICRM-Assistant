package com.aicrm.app;

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
class OpenApiDocumentationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void swaggerUiIsAccessibleWithoutAuth() throws Exception {
        // given
        String swaggerUiPath = "/swagger-ui/index.html";

        // when & then
        mockMvc.perform(get(swaggerUiPath))
                .andExpect(status().isOk());
    }

    @Test
    void apiDocsContainsAuthCustomerAgentAndAttachmentTags() throws Exception {
        // given
        String apiDocsPath = "/v3/api-docs";

        // when & then
        mockMvc.perform(get(apiDocsPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("AICRM Core API"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.type").value("http"))
                .andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
                .andExpect(jsonPath("$.paths['/api/auth/login']").exists())
                .andExpect(jsonPath("$.paths['/api/customer/inquiries']").exists())
                .andExpect(jsonPath("$.paths['/api/agent/tickets/waiting']").exists())
                .andExpect(jsonPath("$.paths['/api/tickets/{ticketId}/attachments']").exists())
                .andExpect(jsonPath("$.paths['/api/attachments/{attachmentId}/download-url']").exists());
    }
}
