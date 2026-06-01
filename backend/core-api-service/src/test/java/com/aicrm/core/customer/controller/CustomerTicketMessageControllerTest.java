package com.aicrm.core.customer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.message.application.SaveCustomerTicketMessageService;
import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.domain.SenderType;
import com.aicrm.core.message.dto.SaveMessageResponse;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CustomerTicketMessageControllerTest {

    @Mock
    private SaveCustomerTicketMessageService saveCustomerTicketMessageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomerTicketMessageController(saveCustomerTicketMessageService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void saveMessageReturnsCreatedCustomerMessage() throws Exception {
        // given
        when(saveCustomerTicketMessageService.save(eq(10L), any())).thenReturn(new SaveMessageResponse(
                100L,
                10L,
                SenderType.CUSTOMER,
                MessageType.TEXT,
                "추가 문의 내용입니다.",
                Instant.parse("2026-05-31T11:00:00Z")
        ));

        // when & then
        mockMvc.perform(post("/api/customer/tickets/{ticketId}/messages", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "추가 문의 내용입니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.messageId").value(100))
                .andExpect(jsonPath("$.data.senderType").value("CUSTOMER"))
                .andExpect(jsonPath("$.data.messageType").value("TEXT"));

        verify(saveCustomerTicketMessageService).save(eq(10L), any());
    }

    @Test
    void saveMessageReturnsBadRequestWhenTicketClosed() throws Exception {
        // given
        when(saveCustomerTicketMessageService.save(eq(10L), any()))
                .thenThrow(new BusinessException(ErrorCode.TICKET_CANNOT_ADD_MESSAGE));

        // when & then
        mockMvc.perform(post("/api/customer/tickets/{ticketId}/messages", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "추가 문의"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("TICKET_CANNOT_ADD_MESSAGE"));
    }
}
