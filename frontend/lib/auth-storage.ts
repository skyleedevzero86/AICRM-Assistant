"use client";

import type { UserRole } from "./api/types";

const AUTH_TOKEN_KEY = "aicrm.auth.accessToken";

export function getAccessToken(): string | null {
  if (typeof window === "undefined") {
    return null;
  }
  return window.localStorage.getItem(AUTH_TOKEN_KEY);
}

export function setAccessToken(token: string): void {
  if (typeof window === "undefined") {
    return;
  }
  window.localStorage.setItem(AUTH_TOKEN_KEY, token);
}

export function clearAccessToken(): void {
  if (typeof window === "undefined") {
    return;
  }
  window.localStorage.removeItem(AUTH_TOKEN_KEY);
}

export function getAccessTokenRole(): UserRole | null {
  const token = getAccessToken();
  if (!token) {
    return null;
  }
  const parts = token.split(".");
  if (parts.length < 2) {
    return null;
  }
  try {
    const payload = JSON.parse(atob(parts[1].replace(/-/g, "+").replace(/_/g, "/"))) as { role?: unknown };
    if (payload.role === "ADMIN" || payload.role === "AGENT" || payload.role === "CUSTOMER") {
      return payload.role;
    }
    return null;
  } catch {
    return null;
  }
}
