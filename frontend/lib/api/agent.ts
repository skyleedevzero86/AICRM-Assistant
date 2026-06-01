import { DEFAULT_AGENT_ID } from "../constants";
import { apiRequest } from "./client";
import type {
  AcceptTicketResponse,
  AgentTicketDetail,
  CloseTicketRequest,
  CloseTicketResponse,
  SaveMessageRequest,
  SaveMessageResponse,
  WaitingTicket
} from "./types";
import type { Message } from "./types";

function agentHeaders(): Record<string, string> {
  return { "X-Agent-Id": DEFAULT_AGENT_ID };
}

export function fetchWaitingTickets(): Promise<WaitingTicket[]> {
  return apiRequest<WaitingTicket[]>("/api/agent/tickets/waiting", {
    headers: agentHeaders()
  });
}

export function fetchAgentTicketDetail(ticketId: number): Promise<AgentTicketDetail> {
  return apiRequest<AgentTicketDetail>(`/api/agent/tickets/${ticketId}`, {
    headers: agentHeaders()
  });
}

export function acceptTicket(ticketId: number): Promise<AcceptTicketResponse> {
  return apiRequest<AcceptTicketResponse>(`/api/agent/tickets/${ticketId}/accept`, {
    method: "POST",
    headers: agentHeaders()
  });
}

export function closeTicket(ticketId: number, request: CloseTicketRequest): Promise<CloseTicketResponse> {
  return apiRequest<CloseTicketResponse>(`/api/agent/tickets/${ticketId}/close`, {
    method: "POST",
    body: request,
    headers: agentHeaders()
  });
}

export function saveAgentMessage(
  ticketId: number,
  request: SaveMessageRequest
): Promise<SaveMessageResponse> {
  return apiRequest<SaveMessageResponse>(`/api/agent/tickets/${ticketId}/messages`, {
    method: "POST",
    body: request,
    headers: agentHeaders()
  });
}

export function fetchTicketMessages(ticketId: number): Promise<Message[]> {
  return apiRequest<Message[]>(`/api/tickets/${ticketId}/messages`);
}
