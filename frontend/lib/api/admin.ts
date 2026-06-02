import { apiRequest } from "./client";
import type { AdminAgentAttendance, AdminAgentUser, AdminCustomerUser, AgentApprovalStatus, AgentGrade } from "./types";

export function fetchAdminCustomers(keyword: string): Promise<AdminCustomerUser[]> {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return apiRequest<AdminCustomerUser[]>(`/api/admin/users/customers${query}`);
}

export function fetchAdminAgents(
  keyword: string,
  approvalStatus?: AgentApprovalStatus,
  grade?: AgentGrade
): Promise<AdminAgentUser[]> {
  const params = new URLSearchParams();
  if (keyword) params.set("keyword", keyword);
  if (approvalStatus) params.set("approvalStatus", approvalStatus);
  if (grade) params.set("grade", grade);
  const query = params.toString();
  return apiRequest<AdminAgentUser[]>(`/api/admin/users/agents${query ? `?${query}` : ""}`);
}

export function updateSuspension(userId: number, value: "Y" | "N"): Promise<void> {
  return apiRequest<void>(`/api/admin/users/${userId}/suspension`, {
    method: "POST",
    body: { value }
  });
}

export function updateWithdrawal(userId: number, value: "Y" | "N"): Promise<void> {
  return apiRequest<void>(`/api/admin/users/${userId}/withdrawal`, {
    method: "POST",
    body: { value }
  });
}

export function approveAgent(agentId: number): Promise<void> {
  return apiRequest<void>(`/api/admin/agents/${agentId}/approve`, {
    method: "POST"
  });
}

export function updateAgentGrade(agentId: number, grade: "ADMIN" | "COUNSELOR" | "TEAM_LEAD"): Promise<void> {
  return apiRequest<void>(`/api/admin/agents/${agentId}/grade`, {
    method: "POST",
    body: { grade }
  });
}

export function fetchAdminAgentAttendance(keyword: string): Promise<AdminAgentAttendance[]> {
  const query = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  return apiRequest<AdminAgentAttendance[]>(`/api/admin/attendance/agents${query}`);
}

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

export function updateAdminCustomer(
  userId: number,
  request: AdminUpdateCustomerRequest
): Promise<AdminCustomerUser> {
  return apiRequest<AdminCustomerUser>(`/api/admin/users/customers/${userId}`, {
    method: "PATCH",
    body: request
  });
}

export function updateAdminAgent(userId: number, request: AdminUpdateAgentRequest): Promise<AdminAgentUser> {
  return apiRequest<AdminAgentUser>(`/api/admin/users/agents/${userId}`, {
    method: "PATCH",
    body: request
  });
}
