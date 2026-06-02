"use client";

import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { login } from "@/lib/api/auth";
import { ApiError } from "@/lib/api/client";
import { clearAccessToken, setAccessToken } from "@/lib/auth-storage";

function getLoginErrorMessage(error: ApiError): string {
  if (error.code === "AUTH_FAILED") {
    return "이메일 또는 비밀번호가 올바르지 않습니다.";
  }
  if (error.code === "AGENT_APPROVAL_REQUIRED") {
    return "상담원 계정은 관리자 승인 후 로그인할 수 있습니다.";
  }
  if (error.code === "ACCOUNT_SUSPENDED") {
    return "정지된 계정입니다. 관리자에게 문의해 주세요.";
  }
  if (error.code === "ACCOUNT_WITHDRAWN") {
    return "탈퇴 처리된 계정입니다.";
  }
  if (error.code === "UNAUTHORIZED") {
    return "로그인 정보가 만료되었습니다. 다시 로그인해 주세요.";
  }
  return error.message;
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
      setErrorMessage("이메일과 비밀번호를 입력해 주세요.");
      return;
    }

    setPending(true);
    try {
      clearAccessToken();
      const response = await login({ email: normalizedEmail, password });
      setAccessToken(response.accessToken);
      if (response.role === "ADMIN") {
        router.push("/admin/users/agents" as Route);
      } else if (response.role === "AGENT") {
        router.push("/agent/tickets" as Route);
      } else {
        router.push("/customer/inquiry" as Route);
      }
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(getLoginErrorMessage(error));
      } else {
        setErrorMessage("로그인에 실패했습니다.");
      }
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
            <label className="text-sm font-medium text-zinc-700" htmlFor="email">
              이메일
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
              비밀번호
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
            {pending ? "로그인 중..." : "로그인"}
          </button>
        </form>

        <div className="flex gap-4 text-sm">
          <Link className="text-teal-700 hover:underline" href={"/auth/signup/customer" as Route}>
            고객 회원가입
          </Link>
          <Link className="text-teal-700 hover:underline" href={"/auth/signup/agent" as Route}>
            상담원 회원가입
          </Link>
        </div>
      </div>
    </PageShell>
  );
}
