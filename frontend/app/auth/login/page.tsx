"use client";

import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { ApiError } from "@/lib/api/client";
import { login } from "@/lib/api/auth";
import { clearAccessToken, setAccessToken } from "@/lib/auth-storage";
import { msg, resolveLoginError } from "@/lib/messages";

function resolveReturnPath(returnUrl: string | null): Route | null {
  if (!returnUrl || !returnUrl.startsWith("/") || returnUrl.startsWith("//")) {
    return null;
  }
  return returnUrl as Route;
}

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setErrorMessage(null);

    const normalizedEmail = email.trim().toLowerCase();
    if (!normalizedEmail || !password) {
      setErrorMessage(msg.ui("auth.loginRequiredFields"));
      return;
    }

    setPending(true);
    try {
      clearAccessToken();
      const response = await login({ email: normalizedEmail, password });
      setAccessToken(response.accessToken);
      const returnPath = resolveReturnPath(
        typeof window === "undefined" ? null : new URLSearchParams(window.location.search).get("returnUrl")
      );
      if (returnPath) {
        router.push(returnPath);
        return;
      }
      if (response.role === "ADMIN") {
        router.push("/admin/users/agents" as Route);
      } else if (response.role === "AGENT") {
        router.push("/" as Route);
      } else {
        router.push("/customer/inquiry" as Route);
      }
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(resolveLoginError(error.code, error.message));
      } else {
        setErrorMessage(msg.ui("auth.loginFailed"));
      }
    } finally {
      setPending(false);
    }
  }

  return (
    <PageShell title={msg.ui("auth.loginTitle")} description={msg.ui("auth.loginDescription")}>
      <div className="max-w-lg space-y-4">
        {errorMessage ? (
          <AlertBanner
            message={errorMessage}
            variant="error"
          />
        ) : null}

        <form className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6" onSubmit={handleSubmit}>
          <div className="space-y-1">
            <label className="text-sm font-medium text-zinc-700" htmlFor="email">
              {msg.ui("common.email")}
            </label>
            <input
              autoComplete="email"
              className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600"
              id="email"
              onChange={(event) => setEmail(event.target.value)}
              type="email"
              value={email}
            />
          </div>
          <div className="space-y-1">
            <label className="text-sm font-medium text-zinc-700" htmlFor="password">
              {msg.ui("common.password")}
            </label>
            <input
              autoComplete="current-password"
              className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600"
              id="password"
              onChange={(event) => setPassword(event.target.value)}
              type="password"
              value={password}
            />
          </div>
          <button
            className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
            disabled={pending}
            type="submit"
          >
            {pending ? msg.ui("auth.loginPending") : msg.ui("common.login")}
          </button>
        </form>

        <div className="flex gap-4 text-sm">
          <Link className="text-teal-700 hover:underline" href={"/auth/signup/customer" as Route}>
            {msg.ui("auth.signupCustomer")}
          </Link>
          <Link className="text-teal-700 hover:underline" href={"/auth/signup/agent" as Route}>
            {msg.ui("auth.signupAgent")}
          </Link>
        </div>
      </div>
    </PageShell>
  );
}
