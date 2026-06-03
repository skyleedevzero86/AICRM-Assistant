import type { ApiResponse } from "./types";
import { buildApiUrl } from "./base-url";
import { clearAccessToken, getAccessToken } from "../auth-storage";
import { msg } from "../messages";
import { resolveLoginError } from "../../../shared/messages/index";
import { isPublicApiPath, requiresAuthToken } from "./requires-auth";

export const AUTH_REQUIRED_CODE = "AUTH_REQUIRED";

export class ApiError extends Error {
  constructor(
    message: string,
    readonly code?: string
  ) {
    super(message);
    this.name = "ApiError";
  }
}

type RequestOptions = {
  method?: string;
  body?: unknown;
  headers?: Record<string, string>;
};

function redirectToLoginForProtectedPage(): void {
  if (typeof window === "undefined") {
    return;
  }
  const { pathname, search } = window.location;
  if (
    !pathname.startsWith("/admin") &&
    !pathname.startsWith("/agent") &&
    !pathname.startsWith("/customer") &&
    !pathname.startsWith("/account")
  ) {
    return;
  }
  const returnUrl = encodeURIComponent(`${pathname}${search}`);
  window.location.replace(`/auth/login?returnUrl=${returnUrl}`);
}

function resolveFailureMessage(path: string, status: number, payload: ApiResponse<unknown> | null): string {
  const code = payload?.error?.code;
  const message = payload?.error?.message;

  if (status >= 502 || status === 0) {
    return msg.client("BACKEND_UNAVAILABLE");
  }

  if (isPublicApiPath(path)) {
    return resolveLoginError(code, message);
  }

  if (status >= 500) {
    return msg.client("BACKEND_UNAVAILABLE");
  }

  return message ?? msg.client("REQUEST_FAILED");
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, headers = {} } = options;
  const token = getAccessToken();
  const shouldAttachToken = token && !isPublicApiPath(path);
  const url = buildApiUrl(path);

  if (requiresAuthToken(path) && !token) {
    throw new ApiError(msg.client("AUTH_REQUIRED"), AUTH_REQUIRED_CODE);
  }

  let response: Response;
  try {
    response = await fetch(url, {
      method,
      headers: {
        "Content-Type": "application/json",
        ...(shouldAttachToken ? { Authorization: `Bearer ${token}` } : {}),
        ...headers
      },
      body: body === undefined ? undefined : JSON.stringify(body)
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

  if (response.status === 401 && !isPublicApiPath(path)) {
    clearAccessToken();
    redirectToLoginForProtectedPage();
    const unauthorizedMessage = payload?.error?.message ?? msg.error("UNAUTHORIZED");
    throw new ApiError(unauthorizedMessage, payload?.error?.code ?? "UNAUTHORIZED");
  }

  if (!response.ok || !payload?.success) {
    const message = resolveFailureMessage(path, response.status, payload);
    throw new ApiError(message, payload?.error?.code ?? "REQUEST_FAILED");
  }

  return payload.data;
}
