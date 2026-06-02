"use client";

import type { Route } from "next";
import Link from "next/link";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { signupAgent } from "@/lib/api/auth";
import { ApiError } from "@/lib/api/client";

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
      setErrorMessage(error instanceof ApiError ? error.message : "회원가입에 실패했습니다.");
    } finally {
      setPending(false);
    }
  }

  return (
    <PageShell title="상담원 회원가입" description="상담원 계정은 관리자 승인 후 로그인할 수 있습니다.">
      <div className="max-w-lg space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        {done ? <AlertBanner message="회원가입이 접수되었습니다. 관리자 승인 후 로그인할 수 있습니다." variant="success" /> : null}
        <form className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6" onSubmit={submit}>
          <input className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm" onChange={(e) => setName(e.target.value)} placeholder="이름" value={name} />
          <input
            className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm"
            inputMode="numeric"
            maxLength={16}
            onChange={(e) => setEmployeeNo(e.target.value.replace(/\D/g, "").slice(0, 16))}
            placeholder="사원번호 (예: 2026060207120101)"
            value={employeeNo}
          />
          <p className="text-xs text-zinc-500">연월일(8) + 시분초(6) + 순번(2) = 16자리 숫자</p>
          <input className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm" onChange={(e) => setEmail(e.target.value)} placeholder="이메일" type="email" value={email} />
          <input className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm" onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호(8자 이상)" type="password" value={password} />
          <button className="rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white disabled:opacity-50" disabled={pending} type="submit">
            {pending ? "접수 중..." : "상담원 회원가입"}
          </button>
        </form>
        <Link className="text-sm text-teal-700 hover:underline" href={"/auth/login" as Route}>로그인으로 이동</Link>
      </div>
    </PageShell>
  );
}
