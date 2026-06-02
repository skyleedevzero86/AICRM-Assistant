"use client";

import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { login } from "@/lib/api/auth";
import { ApiError } from "@/lib/api/client";
import { setAccessToken } from "@/lib/auth-storage";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    setErrorMessage(null);
    setPending(true);
    try {
      const response = await login({ email: email.trim(), password });
      setAccessToken(response.accessToken);
      if (response.role === "ADMIN") {
        router.push("/admin/users/agents" as Route);
      } else if (response.role === "AGENT") {
        router.push("/agent/tickets" as Route);
      } else {
        router.push("/customer/inquiry" as Route);
      }
    } catch (error) {
      setErrorMessage(error instanceof ApiError ? error.message : "로그인에 실패했습니다.");
    } finally {
      setPending(false);
    }
  }

  return (
    <PageShell title="로그인" description="이메일과 비밀번호로 로그인하세요.">
      <div className="max-w-lg space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        <form className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6" onSubmit={handleSubmit}>
          <div className="space-y-1">
            <label className="text-sm font-medium text-zinc-700" htmlFor="email">이메일</label>
            <input
              className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
              id="email"
              onChange={(event) => setEmail(event.target.value)}
              type="email"
              value={email}
            />
          </div>
          <div className="space-y-1">
            <label className="text-sm font-medium text-zinc-700" htmlFor="password">비밀번호</label>
            <input
              className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
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
            {pending ? "로그인 중..." : "로그인"}
          </button>
        </form>
        <div className="flex gap-4 text-sm">
          <Link className="text-teal-700 hover:underline" href={"/auth/signup/customer" as Route}>고객 회원가입</Link>
          <Link className="text-teal-700 hover:underline" href={"/auth/signup/agent" as Route}>상담원 회원가입</Link>
        </div>
      </div>
    </PageShell>
  );
}
