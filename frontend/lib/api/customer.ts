import { apiRequest } from "./client";
import type {
  CreateCustomerInquiryRequest,
  CreateCustomerInquiryResponse,
  CustomerTicketDetail,
  CustomerTicketSummary,
  Message,
  UpdateCustomerInquiryRequest
} from "./types";

export function createCustomerInquiry(
  request: CreateCustomerInquiryRequest
): Promise<CreateCustomerInquiryResponse> {
  return apiRequest<CreateCustomerInquiryResponse>("/api/customer/inquiries", {
    method: "POST",
    body: request
  });
}

export function fetchCustomerTickets(): Promise<CustomerTicketSummary[]> {
  return apiRequest<CustomerTicketSummary[]>("/api/customer/tickets");
}

export function fetchCustomerTicket(ticketId: number): Promise<CustomerTicketDetail> {
  return apiRequest<CustomerTicketDetail>(`/api/customer/tickets/${ticketId}`);
}

export function updateCustomerInquiry(
  ticketId: number,
  request: UpdateCustomerInquiryRequest
): Promise<CustomerTicketDetail> {
  return apiRequest<CustomerTicketDetail>(`/api/customer/tickets/${ticketId}`, {
    method: "PUT",
    body: request
  });
}

export function fetchCustomerTicketMessages(ticketId: number): Promise<Message[]> {
  return apiRequest<Message[]>(`/api/tickets/${ticketId}/messages`);
}
