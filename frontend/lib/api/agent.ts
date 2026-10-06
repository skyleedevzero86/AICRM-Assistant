import { apiRequest } from "./client";
import type {
  AcceptTicketResponse,
  AgentTicketDetail,
  AgentTicketSummary,
  CloseTicketRequest,
  CloseTicketResponse,
  SaveMessageRequest,
  SaveMessageResponse,
  WaitingTicket
} from "./types";
import type { Message } from "./types";

export function fetchWaitingTickets(): Promise<WaitingTicket[]> {
  return apiRequest<WaitingTicket[]>("/api/agent/tickets/waiting");
}

export function fetchAgentTickets(): Promise<AgentTicketSummary[]> {
  return apiRequest<AgentTicketSummary[]>("/api/agent/tickets");
}

export function fetchAgentTicketDetail(ticketId: number): Promise<AgentTicketDetail> {
  return apiRequest<AgentTicketDetail>(`/api/agent/tickets/${ticketId}`);
}

export function acceptTicket(ticketId: number): Promise<AcceptTicketResponse> {
  return apiRequest<AcceptTicketResponse>(`/api/agent/tickets/${ticketId}/accept`, { method: "POST" });
}

export function closeTicket(ticketId: number, request: CloseTicketRequest): Promise<CloseTicketResponse> {
  return apiRequest<CloseTicketResponse>(`/api/agent/tickets/${ticketId}/close`, {
    method: "POST",
    body: request
  });
}

export function saveAgentMessage(
  ticketId: number,
  request: SaveMessageRequest
): Promise<SaveMessageResponse> {
  return apiRequest<SaveMessageResponse>(`/api/agent/tickets/${ticketId}/messages`, {
    method: "POST",
    body: request
  });
}

export function fetchTicketMessages(ticketId: number): Promise<Message[]> {
  return apiRequest<Message[]>(`/api/tickets/${ticketId}/messages`);
}
