const PUBLIC_API_PATHS = new Set([
  "/api/auth/login",
  "/api/auth/signup/customer",
  "/api/auth/signup/agent"
]);

export function isPublicApiPath(path: string): boolean {
  return PUBLIC_API_PATHS.has(path.split("?")[0] ?? path);
}

export function requiresAuthToken(path: string): boolean {
  return path.startsWith("/api/") && !isPublicApiPath(path);
}
