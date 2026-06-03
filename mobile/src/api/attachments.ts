import { apiRequest, ApiError, getApiBaseUrl } from "@/api/client";
import { clearAccessToken, getAccessToken } from "@/storage/authStorage";
import { msg } from "@/messages";
import { requiresAuthToken } from "@/api/requires-auth";
import type { Attachment, AttachmentDownloadUrl } from "@/api/types";

type ApiResponse<T> = {
  success: boolean;
  data: T;
  error: { code: string; message: string } | null;
};

export async function fetchTicketAttachments(ticketId: number): Promise<Attachment[]> {
  return apiRequest<Attachment[]>(`/api/tickets/${ticketId}/attachments`);
}

export async function fetchAttachmentDownloadUrl(attachmentId: number): Promise<AttachmentDownloadUrl> {
  return apiRequest<AttachmentDownloadUrl>(`/api/attachments/${attachmentId}/download-url`);
}

export async function uploadCustomerTicketAttachment(
  ticketId: number,
  file: { uri: string; name: string; mimeType?: string | null },
  messageId?: number
): Promise<Attachment> {
  const formData = new FormData();
  formData.append("file", {
    uri: file.uri,
    name: file.name,
    type: file.mimeType ?? "application/octet-stream"
  } as unknown as Blob);
  if (messageId != null) {
    formData.append("messageId", String(messageId));
  }
  return apiUpload<Attachment>(`/api/customer/tickets/${ticketId}/attachments`, formData);
}

async function apiUpload<T>(path: string, formData: FormData): Promise<T> {
  const token = await getAccessToken();

  if (requiresAuthToken(path) && !token) {
    throw new ApiError(msg.client("AUTH_REQUIRED"), "AUTH_REQUIRED");
  }

  const url = `${getApiBaseUrl()}${path}`;
  let response: Response;

  try {
    response = await fetch(url, {
      method: "POST",
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      body: formData
    });
  } catch {
    throw new ApiError(msg.client("CONNECTION_FAILED", { 0: url }));
  }

  const raw = await response.text();
  let payload: ApiResponse<T>;

  try {
    payload = JSON.parse(raw) as ApiResponse<T>;
  } catch {
    throw new ApiError(msg.client("RESPONSE_PARSE_FAILED", { 0: url }));
  }

  if (response.status === 401) {
    await clearAccessToken();
    throw new ApiError(payload.error?.message ?? msg.error("UNAUTHORIZED"), payload.error?.code ?? "UNAUTHORIZED");
  }

  if (!response.ok || !payload.success) {
    throw new ApiError(payload.error?.message ?? msg.client("REQUEST_FAILED"), payload.error?.code);
  }

  return payload.data;
}
