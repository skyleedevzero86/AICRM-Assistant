package com.aicrm.core.agent.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aicrm.core.agent.application.AcceptTicketService;
import com.aicrm.core.agent.application.CloseTicketService;
import com.aicrm.core.agent.application.GetAgentTicketDetailService;
import com.aicrm.core.agent.application.GetAgentTicketsService;
import com.aicrm.core.agent.application.GetWaitingTicketsService;
import com.aicrm.core.agent.dto.AcceptTicketResponse;
import com.aicrm.core.agent.dto.AgentTicketDetailResponse;
import com.aicrm.core.agent.dto.CloseTicketResponse;
import com.aicrm.core.agent.dto.WaitingTicketResponse;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.global.exception.GlobalExceptionHandler;
import com.aicrm.core.global.response.ApiResponse;
import com.aicrm.core.support.MessagesInitializer;
import com.aicrm.core.ticket.domain.ChannelType;
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
class AgentTicketControllerTest {

    @Mock
    private GetWaitingTicketsService getWaitingTicketsService;

    @Mock
    private GetAgentTicketsService getAgentTicketsService;

    @Mock
    private GetAgentTicketDetailService getAgentTicketDetailService;

    @Mock
    private AcceptTicketService acceptTicketService;

    @Mock
    private CloseTicketService closeTicketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MessagesInitializer.init();
        mockMvc = MockMvcBuilders.standaloneSetup(new AgentTicketController(
                        getWaitingTicketsService,
                        getAgentTicketsService,
                        getAgentTicketDetailService,
                        acceptTicketService,
                        closeTicketService
                ))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getWaitingTicketsReturnsWaitingList() throws Exception {
        // given
        when(getWaitingTicketsService.getWaitingTickets()).thenReturn(List.of(
                new WaitingTicketResponse(
                        1L,
                        "TICKET-20260531-0001",
                        "로그인 문의",
                        "김고객",
                        "일반 문의",
                        ChannelType.WEB_INQUIRY,
                        Instant.parse("2026-05-31T10:00:00Z")
                )
        ));

        // when & then
        mockMvc.perform(get("/api/agent/tickets/waiting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].ticketId").value(1))
                .andExpect(jsonPath("$.data[0].ticketNo").value("TICKET-20260531-0001"));
    }

    @Test
    void getTicketDelegatesToApplicationService() {
        // given
        when(getAgentTicketDetailService.getDetail(1L)).thenReturn(ticketDetail());

        ApiResponse<AgentTicketDetailResponse> response = new AgentTicketController(
                getWaitingTicketsService,
                getAgentTicketsService,
                getAgentTicketDetailService,
                acceptTicketService,
                closeTicketService
        ).getTicket(1L);

        // then
        assertThat(response.success()).isTrue();
        assertThat(response.data().ticketId()).isEqualTo(1L);
        verify(getAgentTicketDetailService).getDetail(1L);
    }

    @Test
    void getTicketReturnsDetail() throws Exception {
        // given
        when(getAgentTicketDetailService.getDetail(anyLong())).thenReturn(ticketDetail());

        // when & then
        mockMvc.perform(get("/api/agent/tickets/{ticketId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ticketId").value(1))
                .andExpect(jsonPath("$.data.inquiryContent").value("로그인이 되지 않습니다."));

        verify(getAgentTicketDetailService).getDetail(1L);
    }

    @Test
    void acceptTicketReturnsAcceptedTicket() throws Exception {
        // given
        when(acceptTicketService.accept(anyLong())).thenReturn(new AcceptTicketResponse(
                1L,
                "TICKET-20260531-0001",
                TicketStatus.IN_PROGRESS,
                1L
        ));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.agentId").value(1));

        verify(acceptTicketService).accept(1L);
    }

    @Test
    void acceptTicketReturnsConflictWhenAlreadyAssigned() throws Exception {
        // given
        when(acceptTicketService.accept(anyLong()))
                .thenThrow(new BusinessException(ErrorCode.TICKET_ALREADY_ASSIGNED));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/accept", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("TICKET_ALREADY_ASSIGNED"))
                .andExpect(jsonPath("$.error.message").value("이미 배정된 티켓입니다"));
    }

    @Test
    void closeTicketReturnsClosedTicket() throws Exception {
        // given
        Instant closedAt = Instant.parse("2026-06-01T12:00:00Z");
        when(closeTicketService.close(anyLong(), org.mockito.ArgumentMatchers.any())).thenReturn(new CloseTicketResponse(
                1L,
                "TICKET-20260601-0001",
                TicketStatus.CLOSED,
                "배송 지연 사유를 안내하고 예상 도착일을 전달했습니다.",
                closedAt,
                closedAt
        ));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/close", 1L)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "resolution": "배송 지연 사유를 안내하고 예상 도착일을 전달했습니다."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CLOSED"))
                .andExpect(jsonPath("$.data.resolution").value("배송 지연 사유를 안내하고 예상 도착일을 전달했습니다."))
                .andExpect(jsonPath("$.data.closedAt").exists());

        verify(closeTicketService).close(anyLong(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void closeTicketReturnsForbiddenWhenNotAssignedAgent() throws Exception {
        // given
        when(closeTicketService.close(anyLong(), org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BusinessException(ErrorCode.NOT_ASSIGNED_AGENT));

        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/close", 1L)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "resolution": "처리 완료"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("NOT_ASSIGNED_AGENT"))
                .andExpect(jsonPath("$.error.message").value("이 티켓에 배정된 상담원이 아닙니다"));
    }

    @Test
    void closeTicketReturnsBadRequestWhenResolutionMissing() throws Exception {
        // when & then
        mockMvc.perform(post("/api/agent/tickets/{ticketId}/close", 1L)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "resolution": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    private AgentTicketDetailResponse ticketDetail() {
        return new AgentTicketDetailResponse(
                1L,
                "TICKET-20260531-0001",
                TicketStatus.WAITING,
                "로그인 문의",
                ChannelType.WEB_INQUIRY,
                Instant.parse("2026-05-31T10:00:00Z"),
                "김고객",
                "010-1234-5678",
                "customer@test.com",
                3L,
                "배송 지연",
                "로그인이 되지 않습니다.",
                null,
                null,
                null
        );
    }
}
