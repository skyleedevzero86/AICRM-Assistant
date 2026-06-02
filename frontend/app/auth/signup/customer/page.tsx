"use client";

import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { login, signupCustomer } from "@/lib/api/auth";
import { setAccessToken } from "@/lib/auth-storage";
import { msg, resolveApiError } from "@/lib/messages";

export default function CustomerSignupPage() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setErrorMessage(null);
    setPending(true);
    try {
      await signupCustomer({ name: name.trim(), email: email.trim(), password });
      const loginResponse = await login({ email: email.trim(), password });
      setAccessToken(loginResponse.accessToken);
      router.push("/" as Route);
    } catch (error) {
      setErrorMessage(resolveApiError(error, "auth.signupFailed"));
    } finally {
      setPending(false);
    }
  }

  return (
    <PageShell title={msg.ui("auth.customerSignupTitle")} description={msg.ui("auth.customerSignupDescription")}>
      <div className="max-w-lg space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        <form className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6" onSubmit={submit}>
          <input
            className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
            onChange={(e) => setName(e.target.value)}
            placeholder={msg.ui("common.name")}
            value={name}
          />
          <input
            className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
            onChange={(e) => setEmail(e.target.value)}
            placeholder={msg.ui("common.email")}
            type="email"
            value={email}
          />
          <input
            className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
            onChange={(e) => setPassword(e.target.value)}
            placeholder={msg.ui("common.passwordMin")}
            type="password"
            value={password}
          />
          <button className="rounded-md bg-teal-700 px-4 py-2 text-sm font-medium text-white disabled:opacity-50" disabled={pending} type="submit">
            {pending ? msg.ui("auth.signupPending") : msg.ui("auth.signupCustomer")}
          </button>
        </form>
        <Link className="text-sm text-teal-700 hover:underline" href={"/auth/login" as Route}>
          {msg.ui("auth.goToLogin")}
        </Link>
      </div>
    </PageShell>
  );
}
