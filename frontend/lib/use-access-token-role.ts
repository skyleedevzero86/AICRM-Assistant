"use client";

import { useEffect, useState } from "react";
import type { UserRole } from "./api/types";
import { getAccessTokenRole } from "./auth-storage";

export function useAccessTokenRole(): UserRole | null {
  const [role, setRole] = useState<UserRole | null>(null);

  useEffect(() => {
    setRole(getAccessTokenRole());
  }, []);

  return role;
}
