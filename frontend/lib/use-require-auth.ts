"use client";

import type { Route } from "next";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { getAccessToken } from "./auth-storage";

type AuthStatus = "checking" | "allowed" | "redirecting";

export function useRequireAuth(): { status: AuthStatus } {
  const router = useRouter();
  const [status, setStatus] = useState<AuthStatus>("checking");

  useEffect(() => {
    if (status === "allowed") {
      return;
    }
    if (getAccessToken()) {
      setStatus("allowed");
      return;
    }
    setStatus("redirecting");
    const returnUrl = encodeURIComponent(`${window.location.pathname}${window.location.search}`);
    router.replace(`/auth/login?returnUrl=${returnUrl}` as Route);
  }, [router, status]);

  return { status };
}
