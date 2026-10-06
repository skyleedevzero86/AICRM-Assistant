"use client";

import { useCallback, useEffect, useState } from "react";
import type { UserRole } from "./api/types";
import { getAccessToken, getAccessTokenRole } from "./auth-storage";

const AUTH_TOKEN_KEY = "aicrm.auth.accessToken";

export function useAuthSession() {
  const [role, setRole] = useState<UserRole | null>(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const refresh = useCallback(() => {
    setIsLoggedIn(!!getAccessToken());
    setRole(getAccessTokenRole());
  }, []);

  useEffect(() => {
    refresh();
  }, [refresh]);

  useEffect(() => {
    const onStorage = (event: StorageEvent) => {
      if (event.key === AUTH_TOKEN_KEY) {
        refresh();
      }
    };
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, [refresh]);

  return { role, isLoggedIn, refresh };
}
