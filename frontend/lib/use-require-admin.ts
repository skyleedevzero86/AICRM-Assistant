"use client";

import type { Route } from "next";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { getAccessToken, getAccessTokenRole } from "./auth-storage";

type AdminAuthStatus = "checking" | "allowed" | "redirecting";

export function useRequireAdmin(): { status: AdminAuthStatus } {
  const router = useRouter();
  const [status, setStatus] = useState<AdminAuthStatus>("checking");

  useEffect(() => {
    if (status === "allowed") {
      return;
    }
    const token = getAccessToken();
    const role = getAccessTokenRole();
    if (token && role === "ADMIN") {
      setStatus("allowed");
      return;
    }

    setStatus("redirecting");
    const returnUrl = encodeURIComponent(`${window.location.pathname}${window.location.search}`);
    router.replace(`/auth/login?returnUrl=${returnUrl}` as Route);
  }, [router, status]);

  return { status };
}
