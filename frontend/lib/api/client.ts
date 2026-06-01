import type { ApiResponse } from "./types";

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

export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, headers = {} } = options;

  const response = await fetch(path, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...headers
    },
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  const payload = (await response.json()) as ApiResponse<T>;

  if (!response.ok || !payload.success) {
    const message = payload.error?.message ?? "요청 처리 중 오류가 발생했습니다.";
    throw new ApiError(message, payload.error?.code);
  }

  return payload.data;
}
