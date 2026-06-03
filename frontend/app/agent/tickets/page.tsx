"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useMemo, useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { acceptTicket, fetchAgentTickets, fetchWaitingTickets } from "@/lib/api/agent";
import { ApiError } from "@/lib/api/client";
import type { TicketStatus } from "@/lib/api/types";
import { formatDateTime, formatTicketStatus } from "@/lib/format";
import { useAccessTokenRole } from "@/lib/use-access-token-role";

const HISTORY_FILTERS: Array<{ label: string; value: "ALL" | TicketStatus }> = [
  { label: "전체", value: "ALL" },
  { label: "진행중", value: "IN_PROGRESS" },
  { label: "완료/종료", value: "CLOSED" }
];

export default function AgentTicketsPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [actionError, setActionError] = useState<string | null>(null);
  const [historyFilter, setHistoryFilter] = useState<"ALL" | TicketStatus>("ALL");
  const role = useAccessTokenRole();
  const canAccessAgentTickets = role === "AGENT";

  const waitingQuery = useQuery({
    queryKey: ["waiting-tickets"],
    queryFn: fetchWaitingTickets,
    enabled: canAccessAgentTickets,
    refetchInterval: 15_000
  });

  const myTicketsQuery = useQuery({
    queryKey: ["agent-tickets"],
    queryFn: fetchAgentTickets,
    enabled: canAccessAgentTickets,
    refetchInterval: 15_000
  });

  const filteredHistory = useMemo(() => {
    const tickets = myTicketsQuery.data ?? [];
    if (historyFilter === "ALL") return tickets;
    if (historyFilter === "CLOSED") {
      return tickets.filter((ticket) => ticket.status === "CLOSED" || ticket.status === "RESOLVED");
    }
    return tickets.filter((ticket) => ticket.status === historyFilter);
  }, [historyFilter, myTicketsQuery.data]);

  const acceptMutation = useMutation({
    mutationFn: acceptTicket,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["waiting-tickets"] });
      queryClient.invalidateQueries({ queryKey: ["agent-tickets"] });
      router.push(`/agent/tickets/${data.ticketId}` as Route);
    },
    onError: (error) => {
      setActionError(error instanceof ApiError ? error.message : "티켓 수락에 실패했습니다.");
    }
  });

  const listError = !canAccessAgentTickets
    ? "상담원 권한이 필요합니다."
    : waitingQuery.error instanceof ApiError
      ? waitingQuery.error.message
      : waitingQuery.isError
        ? "대기 티켓 목록을 불러오지 못했습니다."
        : myTicketsQuery.error instanceof ApiError
          ? myTicketsQuery.error.message
          : myTicketsQuery.isError
            ? "내 상담 이력을 불러오지 못했습니다."
            : null;

  return (
    <PageShell title="상담원 티켓" description="대기 티켓을 수락하고 내가 맡은 상담 이력을 확인합니다.">
      <div className="space-y-6">
        {actionError ? <AlertBanner message={actionError} variant="error" /> : null}
        {listError ? <AlertBanner message={listError} variant="error" /> : null}

        <section className="rounded-lg border border-zinc-200 bg-white">
          <div className="border-b border-zinc-100 px-4 py-3">
            <h2 className="font-semibold text-zinc-900">대기 티켓</h2>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="border-b border-zinc-200 bg-zinc-50 text-left text-zinc-600">
                <tr>
                  <th className="px-4 py-3 font-medium">문의 유형</th>
                  <th className="px-4 py-3 font-medium">고객명</th>
                  <th className="px-4 py-3 font-medium">제목</th>
                  <th className="px-4 py-3 font-medium">접수 시간</th>
                  <th className="px-4 py-3 font-medium">작업</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100">
                {waitingQuery.isLoading ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={5}>
                      목록을 불러오는 중...
                    </td>
                  </tr>
                ) : null}
                {!waitingQuery.isLoading && (waitingQuery.data?.length ?? 0) === 0 ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={5}>
                      대기 중인 티켓이 없습니다.
                    </td>
                  </tr>
                ) : null}
                {waitingQuery.data?.map((ticket) => (
                  <tr key={ticket.ticketId}>
                    <td className="px-4 py-3">{ticket.categoryName}</td>
                    <td className="px-4 py-3">{ticket.customerName}</td>
                    <td className="px-4 py-3">
                      <Link className="font-medium text-teal-700 hover:underline" href={`/agent/tickets/${ticket.ticketId}` as Route}>
                        {ticket.subject}
                      </Link>
                      <div className="text-xs text-zinc-500">{ticket.ticketNo}</div>
                    </td>
                    <td className="px-4 py-3">{formatDateTime(ticket.createdAt)}</td>
                    <td className="px-4 py-3">
                      <button
                        className="rounded-md bg-zinc-900 px-3 py-1.5 text-xs font-medium text-white hover:bg-zinc-800 disabled:opacity-50"
                        disabled={acceptMutation.isPending}
                        onClick={() => {
                          setActionError(null);
                          acceptMutation.mutate(ticket.ticketId);
                        }}
                        type="button"
                      >
                        수락
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="rounded-lg border border-zinc-200 bg-white">
          <div className="flex flex-wrap items-center justify-between gap-3 border-b border-zinc-100 px-4 py-3">
            <h2 className="font-semibold text-zinc-900">내 상담 이력</h2>
            <div className="flex flex-wrap gap-2">
              {HISTORY_FILTERS.map((item) => (
                <button
                  className={`rounded-md border px-3 py-1.5 text-xs ${
                    historyFilter === item.value
                      ? "border-teal-700 bg-teal-700 text-white"
                      : "border-zinc-200 bg-white text-zinc-700 hover:bg-zinc-50"
                  }`}
                  key={item.value}
                  onClick={() => setHistoryFilter(item.value)}
                  type="button"
                >
                  {item.label}
                </button>
              ))}
            </div>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="border-b border-zinc-200 bg-zinc-50 text-left text-zinc-600">
                <tr>
                  <th className="px-4 py-3 font-medium">상태</th>
                  <th className="px-4 py-3 font-medium">고객명</th>
                  <th className="px-4 py-3 font-medium">제목</th>
                  <th className="px-4 py-3 font-medium">접수 시간</th>
                  <th className="px-4 py-3 font-medium">종료 시간</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100">
                {myTicketsQuery.isLoading ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={5}>
                      이력을 불러오는 중...
                    </td>
                  </tr>
                ) : null}
                {!myTicketsQuery.isLoading && filteredHistory.length === 0 ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={5}>
                      표시할 상담 이력이 없습니다.
                    </td>
                  </tr>
                ) : null}
                {filteredHistory.map((ticket) => (
                  <tr key={ticket.ticketId}>
                    <td className="px-4 py-3">{formatTicketStatus(ticket.status)}</td>
                    <td className="px-4 py-3">{ticket.customerName}</td>
                    <td className="px-4 py-3">
                      <Link className="font-medium text-teal-700 hover:underline" href={`/agent/tickets/${ticket.ticketId}` as Route}>
                        {ticket.subject}
                      </Link>
                      <div className="text-xs text-zinc-500">{ticket.ticketNo}</div>
                    </td>
                    <td className="px-4 py-3">{formatDateTime(ticket.createdAt)}</td>
                    <td className="px-4 py-3">{ticket.closedAt ? formatDateTime(ticket.closedAt) : "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </PageShell>
  );
}
