package com.aicrm.core.agent.controller;

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
import com.aicrm.core.message.application.SaveAgentTicketMessageService;
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
class AgentTicketMessageControllerTest {

    @Mock
    private SaveAgentTicketMessageService saveAgentTicketMessageService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AgentTicketMessageController(saveAgentTicketMessageService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void saveMessageReturnsCreatedAgentTextMessage() throws Exception {
        // given
        when(saveAgentTicketMessageService.save(eq(10L), any())).thenReturn(new SaveMessageResponse(
                200L,
                10L,
                SenderType.AGENT,
                MessageType.TEXT,
                "확인 후 안내드리겠습니다.",
                Instant.parse("2026-05-31T11:30:00Z")
        ));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/messages", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "확인 후 안내드리겠습니다."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.senderType").value("AGENT"))
                .andExpect(jsonPath("$.data.messageType").value("TEXT"))
                .andExpect(jsonPath("$.data.content").value("확인 후 안내드리겠습니다."));

        verify(saveAgentTicketMessageService).save(eq(10L), any());
    }

    @Test
    void saveMessageReturnsForbiddenWhenNotAssignedAgent() throws Exception {
        // given
        when(saveAgentTicketMessageService.save(eq(10L), any()))
                .thenThrow(new BusinessException(ErrorCode.NOT_ASSIGNED_AGENT));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/messages", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "답변입니다."
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("NOT_ASSIGNED_AGENT"));
    }

    @Test
    void saveMessageReturnsCreatedInternalMemo() throws Exception {
        // given
        when(saveAgentTicketMessageService.save(eq(10L), any())).thenReturn(new SaveMessageResponse(
                201L,
                10L,
                SenderType.AGENT,
                MessageType.INTERNAL_MEMO,
                "내부 확인 필요",
                Instant.parse("2026-05-31T11:35:00Z")
        ));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/messages", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "내부 확인 필요",
                                  "messageType": "INTERNAL_MEMO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.messageType").value("INTERNAL_MEMO"));
    }
}
