"use client";

import type { Route } from "next";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { getAccessToken } from "./auth-storage";
import { useAccessTokenRole } from "./use-access-token-role";
import type { UserRole } from "./api/types";

type RoleAuthStatus = "checking" | "allowed" | "forbidden" | "redirecting";

export function useRequireRole(allowedRoles: UserRole[]): { status: RoleAuthStatus; role: UserRole | null } {
  const router = useRouter();
  const role = useAccessTokenRole();
  const [status, setStatus] = useState<RoleAuthStatus>("checking");

  useEffect(() => {
    if (!getAccessToken()) {
      setStatus("redirecting");
      const returnUrl = encodeURIComponent(`${window.location.pathname}${window.location.search}`);
      router.replace(`/auth/login?returnUrl=${returnUrl}` as Route);
      return;
    }

    if (!role) {
      setStatus("checking");
      return;
    }

    setStatus(allowedRoles.includes(role) ? "allowed" : "forbidden");
  }, [allowedRoles, role, router]);

  return { status, role };
}
