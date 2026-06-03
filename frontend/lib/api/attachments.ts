import { ApiError, apiRequest } from "./client";
import { buildApiUrl } from "./base-url";
import { clearAccessToken, getAccessToken } from "../auth-storage";
import { msg } from "../messages";
import type { ApiResponse } from "./types";
import { requiresAuthToken } from "./requires-auth";

export type Attachment = {
  attachmentId: number;
  ticketId: number;
  messageId: number | null;
  originalFilename: string;
  contentType: string;
  fileSize: number;
  uploadedAt: string;
};

export type AttachmentDownloadUrl = {
  attachmentId: number;
  downloadUrl: string;
  expiresAt: string;
};

export async function fetchTicketAttachments(ticketId: number): Promise<Attachment[]> {
  return apiRequest<Attachment[]>(`/api/tickets/${ticketId}/attachments`);
}

export async function fetchAttachmentDownloadUrl(attachmentId: number): Promise<AttachmentDownloadUrl> {
  return apiRequest<AttachmentDownloadUrl>(`/api/attachments/${attachmentId}/download-url`);
}

export async function uploadCustomerTicketAttachment(
  ticketId: number,
  file: File,
  messageId?: number
): Promise<Attachment> {
  const formData = new FormData();
  formData.append("file", file);
  if (messageId != null) {
    formData.append("messageId", String(messageId));
  }
  return apiUpload<Attachment>(`/api/customer/tickets/${ticketId}/attachments`, formData);
}

export async function uploadAgentTicketAttachment(
  ticketId: number,
  file: File,
  messageId?: number
): Promise<Attachment> {
  const formData = new FormData();
  formData.append("file", file);
  if (messageId != null) {
    formData.append("messageId", String(messageId));
  }
  return apiUpload<Attachment>(`/api/agent/tickets/${ticketId}/attachments`, formData);
}

async function apiUpload<T>(path: string, formData: FormData): Promise<T> {
  const token = getAccessToken();

  if (requiresAuthToken(path) && !token) {
    throw new ApiError(msg.client("AUTH_REQUIRED"), "AUTH_REQUIRED");
  }

  const url = buildApiUrl(path);
  let response: Response;

  try {
    response = await fetch(url, {
      method: "POST",
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      body: formData
    });
  } catch {
    throw new ApiError(msg.client("BACKEND_UNAVAILABLE"), "BACKEND_UNAVAILABLE");
  }

  const raw = await response.text();
  let payload: ApiResponse<T> | null = null;

  if (raw) {
    try {
      payload = JSON.parse(raw) as ApiResponse<T>;
    } catch {
      throw new ApiError(msg.client("BACKEND_UNAVAILABLE"), "BACKEND_UNAVAILABLE");
    }
  }

  if (response.status === 401) {
    clearAccessToken();
    throw new ApiError(payload?.error?.message ?? msg.error("UNAUTHORIZED"), payload?.error?.code ?? "UNAUTHORIZED");
  }

  if (!response.ok || !payload?.success) {
    throw new ApiError(payload?.error?.message ?? msg.client("REQUEST_FAILED"), payload?.error?.code ?? "REQUEST_FAILED");
  }

  return payload.data;
}
