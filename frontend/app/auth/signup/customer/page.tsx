"use client";

import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { login, signupCustomer } from "@/lib/api/auth";
import { ApiError } from "@/lib/api/client";
import { setAccessToken } from "@/lib/auth-storage";

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
      router.push("/customer/inquiry" as Route);
    } catch (error) {
      setErrorMessage(error instanceof ApiError ? error.message : "회원가입에 실패했습니다.");
    } finally {
      setPending(false);
    }
  }

  return (
    <PageShell title="고객 회원가입" description="가입 즉시 고객 계정이 활성화됩니다.">
      <div className="max-w-lg space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        <form className="space-y-4 rounded-lg border border-zinc-200 bg-white p-6" onSubmit={submit}>
          <input className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm" onChange={(e) => setName(e.target.value)} placeholder="이름" value={name} />
          <input className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm" onChange={(e) => setEmail(e.target.value)} placeholder="이메일" type="email" value={email} />
          <input className="w-full rounded-md border border-zinc-300 px-3 py-2 text-sm" onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호(8자 이상)" type="password" value={password} />
          <button className="rounded-md bg-teal-700 px-4 py-2 text-sm font-medium text-white disabled:opacity-50" disabled={pending} type="submit">
            {pending ? "가입 중..." : "고객 회원가입"}
          </button>
        </form>
        <Link className="text-sm text-teal-700 hover:underline" href={"/auth/login" as Route}>로그인으로 이동</Link>
      </div>
    </PageShell>
  );
}
