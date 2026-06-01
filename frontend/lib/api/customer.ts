import { apiRequest } from "./client";
import type { CreateCustomerInquiryRequest, CreateCustomerInquiryResponse } from "./types";

export function createCustomerInquiry(
  request: CreateCustomerInquiryRequest
): Promise<CreateCustomerInquiryResponse> {
  return apiRequest<CreateCustomerInquiryResponse>("/api/customer/inquiries", {
    method: "POST",
    body: request
  });
}
