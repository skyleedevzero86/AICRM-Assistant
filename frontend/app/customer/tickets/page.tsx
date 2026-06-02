"use client";

import type { Route } from "next";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchCustomerTickets } from "@/lib/api/customer";
import { ApiError } from "@/lib/api/client";
import { formatDateTime, formatTicketStatus } from "@/lib/format";
import { useRequireAuth } from "@/lib/use-require-auth";

export default function CustomerTicketsPage() {
  const { status: authStatus } = useRequireAuth();

  const ticketsQuery = useQuery({
    queryKey: ["customer-tickets"],
    queryFn: fetchCustomerTickets,
    enabled: authStatus === "allowed"
  });

  if (authStatus !== "allowed") {
    return null;
  }

  const errorMessage =
    ticketsQuery.error instanceof ApiError
      ? ticketsQuery.error.message
      : ticketsQuery.error
        ? "문의 목록을 불러오지 못했습니다."
        : null;

  return (
    <PageShell
      title="내 문의"
      description="로그인한 계정으로 접수한 문의만 조회됩니다."
    >
      <div className="space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        {ticketsQuery.isLoading ? <p className="text-sm text-zinc-500">불러오는 중...</p> : null}
        {!ticketsQuery.isLoading && !errorMessage && ticketsQuery.data?.length === 0 ? (
          <p className="rounded-lg border border-zinc-200 bg-white p-4 text-sm text-zinc-600">
            표시할 문의가 없습니다.{" "}
            <Link className="font-medium text-teal-700 hover:underline" href={"/customer/inquiry" as Route}>
              문의 접수하기
            </Link>
          </p>
        ) : null}
        {ticketsQuery.data && ticketsQuery.data.length > 0 ? (
          <ul className="divide-y divide-zinc-100 rounded-lg border border-zinc-200 bg-white">
            {ticketsQuery.data.map((ticket) => (
              <li className="px-4 py-3 text-sm" key={ticket.ticketId}>
                <div className="flex flex-wrap items-baseline justify-between gap-2">
                  <div>
                    <span className="font-medium text-zinc-900">{ticket.ticketNo}</span>
                    <span className="mx-2 text-zinc-300">·</span>
                    <span className="text-zinc-700">{ticket.subject}</span>
                    <span className="ml-2 text-xs text-zinc-500">{ticket.categoryName}</span>
                  </div>
                  <span className="text-xs text-zinc-500">{formatTicketStatus(ticket.status)}</span>
                </div>
                <p className="mt-1 text-xs text-zinc-500">{formatDateTime(ticket.createdAt)}</p>
              </li>
            ))}
          </ul>
        ) : null}
      </div>
    </PageShell>
  );
}
