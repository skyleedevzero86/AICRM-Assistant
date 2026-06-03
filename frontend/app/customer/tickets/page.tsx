"use client";

import type { Route } from "next";
import Link from "next/link";
import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchCustomerTickets } from "@/lib/api/customer";
import { ApiError } from "@/lib/api/client";
import type { TicketStatus } from "@/lib/api/types";
import { formatDateTime, formatTicketStatus } from "@/lib/format";
import { useRequireAuth } from "@/lib/use-require-auth";

const FILTERS: Array<{ label: string; value: "ALL" | TicketStatus }> = [
  { label: "전체", value: "ALL" },
  { label: "접수", value: "WAITING" },
  { label: "상담중", value: "IN_PROGRESS" },
  { label: "종료", value: "CLOSED" }
];

function isClosed(status: TicketStatus): boolean {
  return status === "CLOSED" || status === "RESOLVED";
}

export default function CustomerTicketsPage() {
  const { status: authStatus } = useRequireAuth();
  const [filter, setFilter] = useState<"ALL" | TicketStatus>("ALL");

  const ticketsQuery = useQuery({
    queryKey: ["customer-tickets"],
    queryFn: fetchCustomerTickets,
    enabled: authStatus === "allowed"
  });

  const tickets = useMemo(() => {
    const items = ticketsQuery.data ?? [];
    if (filter === "ALL") return items;
    return items.filter((ticket) => ticket.status === filter);
  }, [filter, ticketsQuery.data]);

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
    <PageShell title="내 문의" description="내가 남긴 문의와 상담 진행 상태를 확인합니다.">
      <div className="space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        <div className="flex flex-wrap gap-2">
          {FILTERS.map((item) => (
            <button
              className={`rounded-md border px-3 py-1.5 text-sm ${
                filter === item.value
                  ? "border-teal-700 bg-teal-700 text-white"
                  : "border-zinc-200 bg-white text-zinc-700 hover:bg-zinc-50"
              }`}
              key={item.value}
              onClick={() => setFilter(item.value)}
              type="button"
            >
              {item.label}
            </button>
          ))}
        </div>

        {ticketsQuery.isLoading ? <p className="text-sm text-zinc-500">불러오는 중...</p> : null}
        {!ticketsQuery.isLoading && !errorMessage && ticketsQuery.data?.length === 0 ? (
          <p className="rounded-lg border border-zinc-200 bg-white p-4 text-sm text-zinc-600">
            표시할 문의가 없습니다.{" "}
            <Link className="font-medium text-teal-700 hover:underline" href={"/customer/inquiry" as Route}>
              문의 접수하기
            </Link>
          </p>
        ) : null}

        {tickets.length > 0 ? (
          <ul className="divide-y divide-zinc-100 rounded-lg border border-zinc-200 bg-white">
            {tickets.map((ticket) => (
              <li className="px-4 py-3 text-sm" key={ticket.ticketId}>
                <Link className="block" href={`/customer/tickets/${ticket.ticketId}` as Route}>
                  <div className="flex flex-wrap items-start justify-between gap-2">
                    <div>
                      <p className="font-medium text-zinc-900">{ticket.subject}</p>
                      <p className="mt-1 text-xs text-zinc-500">
                        {ticket.ticketNo} · {ticket.categoryName} · {formatDateTime(ticket.createdAt)}
                      </p>
                    </div>
                    <span className={`rounded-full px-2 py-1 text-xs font-medium ${isClosed(ticket.status) ? "bg-zinc-100 text-zinc-700" : "bg-teal-50 text-teal-700"}`}>
                      {isClosed(ticket.status) ? "상담 종료" : formatTicketStatus(ticket.status)}
                    </span>
                  </div>
                </Link>
              </li>
            ))}
          </ul>
        ) : null}
      </div>
    </PageShell>
  );
}
