"use client";

import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { clearAccessToken } from "@/lib/auth-storage";
import { msg } from "@/lib/messages";
import { useAuthSession } from "@/lib/use-auth-session";

export function HomeSidebar() {
  const router = useRouter();
  const { role, isLoggedIn, refresh } = useAuthSession();

  function logout() {
    clearAccessToken();
    refresh();
    router.push("/" as Route);
  }

  return (
    <nav className="space-y-1 text-sm">
      {role === "AGENT" ? (
        <Link className="flex w-full items-center rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100" href={"/agent/tickets" as Route}>
          {msg.ui("nav.agentTickets")}
        </Link>
      ) : null}
      {role === "CUSTOMER" || !isLoggedIn ? (
        <Link className="flex w-full items-center rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100" href={"/customer/inquiry" as Route}>
          {msg.ui("customer.navInquiry")}
        </Link>
      ) : null}
      {role === "ADMIN" ? (
        <Link className="flex w-full items-center rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100" href={"/admin/users/agents" as Route}>
          {msg.ui("nav.adminUsers")}
        </Link>
      ) : null}
      {isLoggedIn ? (
        <Link className="flex w-full items-center rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100" href={"/account" as Route}>
          {msg.ui("nav.account")}
        </Link>
      ) : null}
      {isLoggedIn ? (
        <button
          className="flex w-full items-center rounded-md px-3 py-2 text-left text-zinc-700 hover:bg-zinc-100"
          onClick={logout}
          type="button"
        >
          {msg.ui("common.logout")}
        </button>
      ) : (
        <Link className="flex w-full items-center rounded-md px-3 py-2 text-zinc-700 hover:bg-zinc-100" href={"/auth/login" as Route}>
          {msg.ui("common.login")}
        </Link>
      )}
      {["지식 문서", "CRM 액션"].map((item) => (
        <span
          className="flex w-full cursor-not-allowed items-center rounded-md px-3 py-2 text-zinc-400"
          key={item}
          title={msg.ui("common.comingSoon")}
        >
          {item}
        </span>
      ))}
    </nav>
  );
}
