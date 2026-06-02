import { getCandidateApiBaseUrls, resolveApiBaseUrl } from "@/config/api-base-url";
import { getAccessToken } from "@/storage/authStorage";

type ApiResponse<T> = {
  success: boolean;
  data: T;
  error: { code: string; message: string } | null;
};

let cachedBaseUrl: string | null = null;

export function getApiBaseUrl(): string {
  return cachedBaseUrl ?? resolveApiBaseUrl();
}

export class ApiError extends Error {
  constructor(
    message: string,
    readonly code?: string
  ) {
    super(message);
    this.name = "ApiError";
  }
}

function isConnectionError(error: unknown): boolean {
  return error instanceof ApiError && error.message.includes("서버에 연결할 수 없습니다");
}

async function apiRequestAt<T>(baseUrl: string, path: string, options: RequestInit = {}): Promise<T> {
  let response: Response;
  const token = await getAccessToken();

  try {
    response = await fetch(`${baseUrl}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers
      }
    });
  } catch {
    throw new ApiError(`서버에 연결할 수 없습니다. (${baseUrl})`);
  }

  const raw = await response.text();
  let payload: ApiResponse<T>;

  try {
    payload = JSON.parse(raw) as ApiResponse<T>;
  } catch {
    throw new ApiError(`서버 응답을 해석할 수 없습니다. (${baseUrl})`);
  }

  if (!response.ok || !payload.success) {
    throw new ApiError(payload.error?.message ?? "요청 처리 중 오류가 발생했습니다.", payload.error?.code);
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
      ? `${lastError.message} 시도한 주소: ${tried}`
      : `서버에 연결할 수 없습니다. 시도한 주소: ${tried}`
  );
}
