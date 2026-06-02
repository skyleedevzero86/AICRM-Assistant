"use client";

import type { Route } from "next";
import Link from "next/link";
import { msg } from "@/lib/messages";
import { useRequireAdmin } from "@/lib/use-require-admin";

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const { status } = useRequireAdmin();

  if (status !== "allowed") {
    return (
      <main className="min-h-screen p-6">
        <div className="mx-auto max-w-4xl rounded-lg border border-zinc-200 bg-white p-6 text-sm text-zinc-600">
          <p>{msg.ui("admin.loginRequired")}</p>
          <Link className="mt-3 inline-block text-teal-700 hover:underline" href={"/auth/login" as Route}>
            {msg.ui("common.login")}
          </Link>
        </div>
      </main>
    );
  }

  return children;
}
