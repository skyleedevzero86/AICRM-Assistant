export function getApiBaseUrl(): string {
  const configured = process.env.NEXT_PUBLIC_API_BASE_URL?.trim();
  if (!configured) {
    return "";
  }
  return configured.replace(/\/$/, "");
}

export function buildApiUrl(path: string): string {
  const base = getApiBaseUrl();
  if (!base) {
    return path;
  }
  return `${base}${path}`;
}
