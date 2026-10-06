package com.aicrm.core.customer.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.customer.application.GetCustomerTicketsService;
import com.aicrm.core.customer.application.UpdateCustomerInquiryService;
import com.aicrm.core.customer.dto.CustomerTicketDetailResponse;
import com.aicrm.core.customer.dto.CustomerTicketSummaryResponse;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.ticket.domain.TicketStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CustomerTicketControllerTest {

    @Mock
    private GetCustomerTicketsService getCustomerTicketsService;

    @Mock
    private UpdateCustomerInquiryService updateCustomerInquiryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                        new CustomerTicketController(getCustomerTicketsService, updateCustomerInquiryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getTicketsReturnsCurrentCustomerTicketList() throws Exception {
        // given
        when(getCustomerTicketsService.getTickets()).thenReturn(List.of(
                new CustomerTicketSummaryResponse(
                        10L,
                        "TICKET-20260531-0001",
                        TicketStatus.WAITING,
                        "배송 문의",
                        "배송",
                        Instant.parse("2026-05-31T10:00:00Z")
                )
        ));

        // when & then
        mockMvc.perform(get("/api/customer/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].ticketId").value(10))
                .andExpect(jsonPath("$.data[0].subject").value("배송 문의"));

        verify(getCustomerTicketsService).getTickets();
    }

    @Test
    void getTicketReturnsOwnedTicketDetail() throws Exception {
        // given
        when(getCustomerTicketsService.getTicket(eq(10L))).thenReturn(new CustomerTicketDetailResponse(
                10L,
                "TICKET-20260531-0001",
                TicketStatus.WAITING,
                "배송 문의",
                3L,
                "배송",
                Instant.parse("2026-05-31T10:00:00Z"),
                "김고객",
                "010-1234-5678",
                "customer@test.com",
                "문의 내용",
                null,
                null
        ));

        // when & then
        mockMvc.perform(get("/api/customer/tickets/{ticketId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ticketId").value(10))
                .andExpect(jsonPath("$.data.inquiryContent").value("문의 내용"));

        verify(getCustomerTicketsService).getTicket(10L);
    }

    @Test
    void getTicketReturnsNotFoundForOtherCustomerTicket() throws Exception {
        // given
        when(getCustomerTicketsService.getTicket(eq(99L)))
                .thenThrow(new BusinessException(ErrorCode.TICKET_NOT_FOUND, "TICKET_NOT_FOUND", 99L));

        // when & then
        mockMvc.perform(get("/api/customer/tickets/{ticketId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("TICKET_NOT_FOUND"));
    }
}
