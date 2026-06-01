package com.aicrm.core.message.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.message.application.GetTicketMessagesService;
import com.aicrm.core.message.domain.MessageType;
import com.aicrm.core.message.domain.SenderType;
import com.aicrm.core.message.dto.MessageResponse;
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
class TicketMessageControllerTest {

    @Mock
    private GetTicketMessagesService getTicketMessagesService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TicketMessageController(getTicketMessagesService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getMessagesReturnsOrderedHistory() throws Exception {
        // given
        when(getTicketMessagesService.getMessages(anyLong())).thenReturn(List.of(
                new MessageResponse(
                        1L,
                        10L,
                        SenderType.CUSTOMER,
                        1L,
                        MessageType.TEXT,
                        "로그인이 되지 않습니다.",
                        Instant.parse("2026-05-31T10:00:00Z")
                ),
                new MessageResponse(
                        2L,
                        10L,
                        SenderType.AGENT,
                        1L,
                        MessageType.TEXT,
                        "확인 후 안내드리겠습니다.",
                        Instant.parse("2026-05-31T10:05:00Z")
                )
        ));

        // when & then
        mockMvc.perform(get("/api/tickets/{ticketId}/messages", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].senderType").value("CUSTOMER"))
                .andExpect(jsonPath("$.data[1].senderType").value("AGENT"))
                .andExpect(jsonPath("$.data[1].content").value("확인 후 안내드리겠습니다."));

        verify(getTicketMessagesService).getMessages(10L);
    }
}
