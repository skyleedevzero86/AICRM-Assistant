"use client";

import type { Route } from "next";
import Link from "next/link";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminTickets } from "@/lib/api/admin";
import { ApiError } from "@/lib/api/client";
import type { TicketStatus } from "@/lib/api/types";
import { formatChannelType, formatDateTime, formatTicketStatus } from "@/lib/format";

const FILTERS: Array<{ label: string; value?: TicketStatus }> = [
  { label: "전체" },
  { label: "미처리", value: "WAITING" },
  { label: "진행중", value: "IN_PROGRESS" },
  { label: "종료", value: "CLOSED" }
];

export default function AdminTicketsPage() {
  const [status, setStatus] = useState<TicketStatus | undefined>();

  const ticketsQuery = useQuery({
    queryKey: ["admin-tickets", status],
    queryFn: () => fetchAdminTickets(status)
  });

  const errorMessage =
    ticketsQuery.error instanceof ApiError
      ? ticketsQuery.error.message
      : ticketsQuery.isError
        ? "상담 이력을 불러오지 못했습니다."
        : null;

  return (
    <PageShell title="상담 이력" description="관리자는 미처리, 진행중, 종료된 상담 이력을 모두 확인합니다.">
      <div className="space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}

        <div className="flex flex-wrap gap-2">
          {FILTERS.map((item) => (
            <button
              className={`rounded-md border px-3 py-1.5 text-sm ${
                status === item.value
                  ? "border-teal-700 bg-teal-700 text-white"
                  : "border-zinc-200 bg-white text-zinc-700 hover:bg-zinc-50"
              }`}
              key={item.label}
              onClick={() => setStatus(item.value)}
              type="button"
            >
              {item.label}
            </button>
          ))}
        </div>

        <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="border-b border-zinc-200 bg-zinc-50 text-left text-zinc-600">
                <tr>
                  <th className="px-4 py-3 font-medium">상태</th>
                  <th className="px-4 py-3 font-medium">고객명</th>
                  <th className="px-4 py-3 font-medium">제목</th>
                  <th className="px-4 py-3 font-medium">채널</th>
                  <th className="px-4 py-3 font-medium">상담원</th>
                  <th className="px-4 py-3 font-medium">접수 시간</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100">
                {ticketsQuery.isLoading ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={6}>
                      상담 이력을 불러오는 중...
                    </td>
                  </tr>
                ) : null}
                {!ticketsQuery.isLoading && (ticketsQuery.data?.length ?? 0) === 0 ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={6}>
                      표시할 상담 이력이 없습니다.
                    </td>
                  </tr>
                ) : null}
                {ticketsQuery.data?.map((ticket) => (
                  <tr key={ticket.ticketId}>
                    <td className="px-4 py-3">{formatTicketStatus(ticket.status)}</td>
                    <td className="px-4 py-3">{ticket.customerName}</td>
                    <td className="px-4 py-3">
                      <Link className="font-medium text-teal-700 hover:underline" href={`/admin/tickets/${ticket.ticketId}` as Route}>
                        {ticket.subject}
                      </Link>
                      <div className="text-xs text-zinc-500">{ticket.ticketNo} · {ticket.categoryName}</div>
                    </td>
                    <td className="px-4 py-3">{formatChannelType(ticket.channel)}</td>
                    <td className="px-4 py-3">{ticket.agentId ? `#${ticket.agentId}` : "미배정"}</td>
                    <td className="px-4 py-3">{formatDateTime(ticket.createdAt)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </PageShell>
  );
}
