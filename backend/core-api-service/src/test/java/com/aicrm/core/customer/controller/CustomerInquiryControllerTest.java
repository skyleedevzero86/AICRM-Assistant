package com.aicrm.core.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.customer.application.CreateCustomerInquiryService;
import com.aicrm.core.customer.dto.CreateCustomerInquiryResponse;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.ticket.domain.TicketStatus;
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
        // given
        when(createCustomerInquiryService.create(any())).thenReturn(new CreateCustomerInquiryResponse(
                1L,
                "TICKET-20260530-0001",
                TicketStatus.WAITING
        ));

        // when & then
        mockMvc.perform(post("/api/customer/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "김고객",
                                  "phone": "010-1234-5678",
                                  "email": "customer@test.com",
                                  "categoryId": 3,
                                  "title": "배송이 늦어요",
                                  "content": "지난주 주문했는데 아직 도착하지 않았습니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticketId").value(1))
                .andExpect(jsonPath("$.data.ticketNo").value("TICKET-20260530-0001"))
                .andExpect(jsonPath("$.data.status").value("WAITING"));
    }

    @Test
    void createReturnsBadRequestWhenCategoryDepthIsInvalid() throws Exception {
        // given
        when(createCustomerInquiryService.create(any()))
                .thenThrow(new BusinessException(ErrorCode.INVALID_CATEGORY_DEPTH, "INVALID_CATEGORY_DEPTH", 1L));

        // when & then
        mockMvc.perform(post("/api/customer/inquiries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "김고객",
                                  "phone": "010-1234-5678",
                                  "email": "customer@test.com",
                                  "categoryId": 1,
                                  "title": "잘못된 카테고리",
                                  "content": "요청이 실패해야 합니다."
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_CATEGORY_DEPTH"));
    }
}
