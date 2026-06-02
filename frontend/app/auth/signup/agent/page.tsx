"use client";

import type { Route } from "next";
import Link from "next/link";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { signupAgent } from "@/lib/api/auth";
import { msg, resolveApiError } from "@/lib/messages";

export default function AgentSignupPage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [employeeNo, setEmployeeNo] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  async function submit(event: React.FormEvent) {
    event.preventDefault();
    setErrorMessage(null);
    setPending(true);
    try {
      await signupAgent({
        name: name.trim(),
        email: email.trim(),
        password,
        employeeNo: employeeNo.trim()
      });
      setDone(true);
    } catch (error) {
      setErrorMessage(resolveApiError(error, "auth.signupFailed"));
    } finally {
      setPending(false);
    }
  }

  return (
    <PageShell title={msg.ui("auth.agentSignupTitle")} description={msg.ui("auth.agentSignupDescription")}>
      <div className="max-w-lg space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        {done ? <AlertBanner message={msg.ui("auth.signupAgentPageSuccess")} variant="success" /> : null}
        <form className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6" onSubmit={submit}>
          <input
            className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
            onChange={(e) => setName(e.target.value)}
            placeholder={msg.ui("common.name")}
            value={name}
          />
          <input
            className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
            inputMode="numeric"
            maxLength={16}
            onChange={(e) => setEmployeeNo(e.target.value.replace(/\D/g, "").slice(0, 16))}
            placeholder={msg.ui("auth.employeeNo")}
            value={employeeNo}
          />
          <p className="text-xs text-zinc-500">{msg.ui("auth.employeeNoHint")}</p>
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
          <button className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50" disabled={pending} type="submit">
            {pending ? msg.ui("auth.signupSubmitting") : msg.ui("auth.agentSignupSubmit")}
          </button>
        </form>
        <Link className="text-sm text-teal-700 hover:underline" href={"/auth/login" as Route}>
          {msg.ui("auth.goToLogin")}
        </Link>
      </div>
    </PageShell>
  );
}
