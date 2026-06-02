import { getCandidateApiBaseUrls, resolveApiBaseUrl } from "@/config/api-base-url";
import { isPublicApiPath, requiresAuthToken } from "@/api/requires-auth";
import { msg } from "@/messages";
import { clearAccessToken, getAccessToken } from "@/storage/authStorage";

type ApiResponse<T> = {
  success: boolean;
  data: T;
  error: { code: string; message: string } | null;
};

let cachedBaseUrl: string | null = null;

export function getApiBaseUrl(): string {
  return cachedBaseUrl ?? resolveApiBaseUrl();
}

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

const CONNECTION_FAILED_CODE = "CONNECTION_FAILED";

function isConnectionError(error: unknown): boolean {
  return error instanceof ApiError && error.code === CONNECTION_FAILED_CODE;
}

async function apiRequestAt<T>(baseUrl: string, path: string, options: RequestInit = {}): Promise<T> {
  let response: Response;
  const token = await getAccessToken();
  const shouldAttachToken = token && !isPublicApiPath(path);

  if (requiresAuthToken(path) && !token) {
    throw new ApiError(msg.client("AUTH_REQUIRED"), AUTH_REQUIRED_CODE);
  }

  try {
    response = await fetch(`${baseUrl}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(shouldAttachToken ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers
      }
    });
  } catch {
    throw new ApiError(msg.client("CONNECTION_FAILED", { 0: baseUrl }), CONNECTION_FAILED_CODE);
  }

  const raw = await response.text();
  let payload: ApiResponse<T>;

  try {
    payload = JSON.parse(raw) as ApiResponse<T>;
  } catch {
    throw new ApiError(msg.client("RESPONSE_PARSE_FAILED", { 0: baseUrl }));
  }

  if (response.status === 401) {
    if (!isPublicApiPath(path)) {
      await clearAccessToken();
    }
    throw new ApiError(
      payload.error?.message ?? msg.error("UNAUTHORIZED"),
      payload.error?.code ?? "UNAUTHORIZED"
    );
  }

  if (!response.ok || !payload.success) {
    throw new ApiError(payload.error?.message ?? msg.client("REQUEST_FAILED"), payload.error?.code);
  }

  return payload.data;
}

export async function apiRequest<T>(path: string, options: RequestInit = {}): Promise<T> {
  const bases = cachedBaseUrl
    ? [cachedBaseUrl, ...getCandidateApiBaseUrls().filter((url) => url !== cachedBaseUrl)]
    : getCandidateApiBaseUrls();

  let lastError: ApiError | null = null;

  for (const baseUrl of bases) {
    try {
      const data = await apiRequestAt<T>(baseUrl, path, options);
      cachedBaseUrl = baseUrl;
      return data;
    } catch (error) {
      if (error instanceof ApiError && isConnectionError(error)) {
        lastError = error;
        continue;
      }
      throw error;
    }
  }

  const tried = bases.join(", ");
  throw new ApiError(
    lastError?.message
      ? msg.client("CONNECTION_FAILED_WITH_TRIED", { 0: lastError.message, 1: tried })
      : msg.client("CONNECTION_FAILED_TRIED", { 0: tried })
  );
}
