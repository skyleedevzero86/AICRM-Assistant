import { apiRequest } from "./client";
import type { AdminAgentUser, AdminCustomerUser } from "./types";

export type AdminUpdateCustomerRequest = {
  name: string;
  email: string;
  phone?: string;
};

export type AdminUpdateAgentRequest = {
  name: string;
  email: string;
  employeeNo: string;
};

export function fetchAdminCustomers(keyword = ""): Promise<AdminCustomerUser[]> {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return apiRequest<AdminCustomerUser[]>(`/api/admin/users/customers${query}`);
}

export function fetchAdminAgents(keyword = ""): Promise<AdminAgentUser[]> {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return apiRequest<AdminAgentUser[]>(`/api/admin/users/agents${query}`);
}

export function updateAdminCustomer(
  userId: number,
  request: AdminUpdateCustomerRequest
): Promise<AdminCustomerUser> {
  return apiRequest<AdminCustomerUser>(`/api/admin/users/customers/${userId}`, {
    method: "PATCH",
    body: JSON.stringify(request)
  });
}

export function updateAdminAgent(userId: number, request: AdminUpdateAgentRequest): Promise<AdminAgentUser> {
  return apiRequest<AdminAgentUser>(`/api/admin/users/agents/${userId}`, {
    method: "PATCH",
    body: JSON.stringify(request)
  });
}
