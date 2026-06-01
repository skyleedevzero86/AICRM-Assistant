import { apiRequest } from "./client";
import type {
  CreateCustomerInquiryRequest,
  CreateCustomerInquiryResponse,
  CustomerTicketDetail,
  CustomerTicketSummary,
  Message,
  SaveMessageRequest,
  SaveMessageResponse
} from "./types";

export function createCustomerInquiry(
  request: CreateCustomerInquiryRequest
): Promise<CreateCustomerInquiryResponse> {
  return apiRequest<CreateCustomerInquiryResponse>("/api/customer/inquiries", {
    method: "POST",
    body: JSON.stringify(request)
  });
}

export function fetchCustomerTickets(): Promise<CustomerTicketSummary[]> {
  return apiRequest<CustomerTicketSummary[]>("/api/customer/tickets");
}

export function fetchCustomerTicket(ticketId: number): Promise<CustomerTicketDetail> {
  return apiRequest<CustomerTicketDetail>(`/api/customer/tickets/${ticketId}`);
}

export function fetchCustomerTicketMessages(ticketId: number): Promise<Message[]> {
  return apiRequest<Message[]>(`/api/tickets/${ticketId}/messages`);
}

export function createCustomerTicketMessage(
  ticketId: number,
  request: SaveMessageRequest
): Promise<SaveMessageResponse> {
  return apiRequest<SaveMessageResponse>(`/api/customer/tickets/${ticketId}/messages`, {
    method: "POST",
    body: JSON.stringify(request)
  });
}
