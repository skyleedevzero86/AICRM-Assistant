import type { ApiResponse } from "./types";
import { clearAccessToken, getAccessToken } from "../auth-storage";
import { msg } from "../messages";
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

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, headers = {} } = options;
  const token = getAccessToken();
  const shouldAttachToken = token && !isPublicApiPath(path);

  if (requiresAuthToken(path) && !token) {
    throw new ApiError(msg.client("AUTH_REQUIRED"), AUTH_REQUIRED_CODE);
  }

  const response = await fetch(path, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(shouldAttachToken ? { Authorization: `Bearer ${token}` } : {}),
      ...headers
    },
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  let payload: ApiResponse<T>;
  try {
    payload = (await response.json()) as ApiResponse<T>;
  } catch {
    if (!response.ok) {
      throw new ApiError(msg.client("BACKEND_UNAVAILABLE"), "BACKEND_UNAVAILABLE");
    }
    throw new ApiError(msg.client("REQUEST_FAILED"), "INVALID_RESPONSE");
  }

  if (response.status === 401) {
    if (!isPublicApiPath(path)) {
      clearAccessToken();
      redirectToLoginForProtectedPage();
    }
    throw new ApiError(
      payload.error?.message ?? msg.error("UNAUTHORIZED"),
      payload.error?.code ?? "UNAUTHORIZED"
    );
  }

  if (!response.ok || !payload.success) {
    if (response.status >= 500) {
      throw new ApiError(msg.client("BACKEND_UNAVAILABLE"), "BACKEND_UNAVAILABLE");
    }
    const message = payload.error?.message ?? msg.client("REQUEST_FAILED");
    throw new ApiError(message, payload.error?.code);
  }

  return payload.data;
}
