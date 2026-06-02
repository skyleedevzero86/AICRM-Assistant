"use client";

import type { Route } from "next";
import Link from "next/link";
import { getAccessTokenRole } from "@/lib/auth-storage";

type PageShellProps = {
  title: string;
  description?: string;
  children: React.ReactNode;
};

export function PageShell({ title, description, children }: PageShellProps) {
  const role = getAccessTokenRole();

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
            {role !== "ADMIN" ? (
              <Link className="hover:text-zinc-900" href={"/agent/tickets" as Route}>
                상담원 티켓
              </Link>
            ) : null}
          </nav>
          <h1 className="text-2xl font-semibold">{title}</h1>
          {description ? <p className="mt-1 text-sm text-zinc-500">{description}</p> : null}
        </header>
        {children}
      </div>
    </main>
  );
}
