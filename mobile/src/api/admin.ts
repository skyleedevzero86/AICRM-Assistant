import { apiRequest } from "./client";
import type { AdminAgentUser, AdminCustomerUser } from "./types";

export function fetchAdminCustomers(keyword = ""): Promise<AdminCustomerUser[]> {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return apiRequest<AdminCustomerUser[]>(`/api/admin/users/customers${query}`);
}

export function fetchAdminAgents(keyword = ""): Promise<AdminAgentUser[]> {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return apiRequest<AdminAgentUser[]>(`/api/admin/users/agents${query}`);
}
