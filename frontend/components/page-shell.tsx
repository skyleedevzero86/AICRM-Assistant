"use client";

import type { Route } from "next";
import Link from "next/link";
import { clearAccessToken } from "@/lib/auth-storage";
import { msg } from "@/lib/messages";
import { useAuthSession } from "@/lib/use-auth-session";
import { useAccessTokenRole } from "@/lib/use-access-token-role";
import { useRouter } from "next/navigation";

type PageShellProps = {
  title: string;
  description?: string;
  children: React.ReactNode;
};

export function PageShell({ title, description, children }: PageShellProps) {
  const router = useRouter();
  const role = useAccessTokenRole();
  const { isLoggedIn } = useAuthSession();

  function logout() {
    clearAccessToken();
    router.push("/auth/login" as Route);
  }

  return (
    <main className="min-h-screen p-6">
      <div className="mx-auto max-w-4xl">
        <header className="mb-6">
          <nav className="mb-4 flex flex-wrap items-center gap-3 text-sm text-zinc-600">
            <Link className="hover:text-zinc-900" href={"/" as Route}>
              홈
            </Link>
            <span className="text-zinc-300" aria-hidden>
              |
            </span>
            <Link className="hover:text-zinc-900" href={"/customer/inquiry" as Route}>
              고객 문의
            </Link>
            {role === "ADMIN" ? (
              <>
                <Link className="hover:text-zinc-900" href={"/admin/users/agents" as Route}>
                  상담사 회원 관리
                </Link>
                <Link className="hover:text-zinc-900" href={"/admin/users/customers" as Route}>
                  고객 회원 관리
                </Link>
                <Link className="hover:text-zinc-900" href={"/admin/attendance" as Route}>
                  근태 관리
                </Link>
              </>
            ) : null}
            {role === "AGENT" ? (
              <Link className="hover:text-zinc-900" href={"/agent/tickets" as Route}>
                상담원 티켓
              </Link>
            ) : null}
            {isLoggedIn ? (
              <>
                <Link className="hover:text-zinc-900" href={"/account" as Route}>
                  {msg.ui("nav.account")}
                </Link>
                <button className="hover:text-zinc-900" onClick={logout} type="button">
                  {msg.ui("common.logout")}
                </button>
              </>
            ) : (
              <Link className="hover:text-zinc-900" href={"/auth/login" as Route}>
                {msg.ui("common.login")}
              </Link>
            )}
          </nav>
          <h1 className="text-2xl font-semibold">{title}</h1>
          {description ? <p className="mt-1 text-sm text-zinc-500">{description}</p> : null}
        </header>
        {children}
      </div>
    </main>
  );
}
