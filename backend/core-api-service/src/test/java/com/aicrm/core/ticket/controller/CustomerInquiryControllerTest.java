package com.aicrm.core.ticket.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.ticket.application.CreateCustomerInquiryService;
import com.aicrm.core.ticket.domain.ChannelType;
import com.aicrm.core.ticket.domain.TicketStatus;
import com.aicrm.core.ticket.dto.CreateInquiryResponse;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerInquiryController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerInquiryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateCustomerInquiryService createCustomerInquiryService;

    @Test
    void createReturnsCreatedResponse() throws Exception {
        Instant createdAt = Instant.parse("2026-05-31T06:00:00Z");
        when(createCustomerInquiryService.create(any())).thenReturn(new CreateInquiryResponse(
                10L,
                20L,
                30L,
                3L,
                "DELIVERY_DELAY",
                "배송 지연",
                TicketStatus.WAITING,
                ChannelType.WEB_INQUIRY,
                "Delivery delayed",
                createdAt
        ));

        mockMvc.perform(post("/api/tickets/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "categoryId": 3,
                                  "subject": "Delivery delayed",
                                  "content": "My package has not arrived.",
                                  "channel": "WEB_INQUIRY"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticketId").value(10))
                .andExpect(jsonPath("$.data.categoryCode").value("DELIVERY_DELAY"));
    }

    @Test
    void createReturnsBadRequestWhenCategoryDepthIsInvalid() throws Exception {
        when(createCustomerInquiryService.create(any()))
                .thenThrow(new BusinessException(
                        ErrorCode.INVALID_CATEGORY_DEPTH,
                        "Consultation category must be a depth-3 leaf category: 1"
                ));

        mockMvc.perform(post("/api/tickets/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerId": 1,
                                  "categoryId": 1,
                                  "subject": "Invalid category",
                                  "content": "This should fail.",
                                  "channel": "WEB_INQUIRY"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_CATEGORY_DEPTH"));
    }
}
