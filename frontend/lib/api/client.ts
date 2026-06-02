import type { ApiResponse } from "./types";
import { clearAccessToken, getAccessToken } from "../auth-storage";
import { msg } from "../messages";
import { requiresAuthToken } from "./requires-auth";

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
  if (!pathname.startsWith("/admin") && !pathname.startsWith("/agent") && !pathname.startsWith("/customer")) {
    return;
  }
  const returnUrl = encodeURIComponent(`${pathname}${search}`);
  window.location.replace(`/auth/login?returnUrl=${returnUrl}`);
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, headers = {} } = options;
  const token = getAccessToken();

  if (requiresAuthToken(path) && !token) {
    throw new ApiError(msg.client("AUTH_REQUIRED"), AUTH_REQUIRED_CODE);
  }

  const response = await fetch(path, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...headers
    },
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  const payload = (await response.json()) as ApiResponse<T>;

  if (response.status === 401) {
    clearAccessToken();
    redirectToLoginForProtectedPage();
    throw new ApiError(
      payload.error?.message ?? msg.error("UNAUTHORIZED"),
      payload.error?.code ?? "UNAUTHORIZED"
    );
  }

  if (!response.ok || !payload.success) {
    const message = payload.error?.message ?? msg.client("REQUEST_FAILED");
    throw new ApiError(message, payload.error?.code);
  }

  return payload.data;
}
