"use client";

import { useAuthSession } from "./use-auth-session";

export function useAccessTokenRole() {
  const { role } = useAuthSession();
  return role;
}
