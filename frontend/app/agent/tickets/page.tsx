"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { acceptTicket, fetchWaitingTickets } from "@/lib/api/agent";
import { ApiError } from "@/lib/api/client";
import { formatDateTime } from "@/lib/format";

export default function AgentTicketsPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const [actionError, setActionError] = useState<string | null>(null);

  const ticketsQuery = useQuery({
    queryKey: ["waiting-tickets"],
    queryFn: fetchWaitingTickets,
    refetchInterval: 15_000
  });

  const acceptMutation = useMutation({
    mutationFn: acceptTicket,
    onSuccess: (data) => {
      queryClient.invalidateQueries({ queryKey: ["waiting-tickets"] });
      router.push(`/agent/tickets/${data.ticketId}` as Route);
    },
    onError: (error) => {
      setActionError(
        error instanceof ApiError ? error.message : "티켓 수락에 실패했습니다."
      );
    }
  });

  const listError =
    ticketsQuery.error instanceof ApiError
      ? ticketsQuery.error.message
      : ticketsQuery.isError
        ? "대기 티켓 목록을 불러오지 못했습니다."
        : null;

  return (
    <PageShell
      title="상담원 대기 티켓"
      description="접수된 문의 티켓을 확인하고 수락할 수 있습니다."
    >
      <div className="space-y-4">
        {actionError ? <AlertBanner message={actionError} variant="error" /> : null}
        {listError ? <AlertBanner message={listError} variant="error" /> : null}

        <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm">
              <thead className="border-b border-zinc-200 bg-zinc-50 text-left text-zinc-600">
                <tr>
                  <th className="px-4 py-3 font-medium">문의 유형</th>
                  <th className="px-4 py-3 font-medium">고객명</th>
                  <th className="px-4 py-3 font-medium">제목</th>
                  <th className="px-4 py-3 font-medium">접수 시간</th>
                  <th className="px-4 py-3 font-medium">티켓 상태</th>
                  <th className="px-4 py-3 font-medium">작업</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100">
                {ticketsQuery.isLoading ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={6}>
                      목록을 불러오는 중...
                    </td>
                  </tr>
                ) : null}
                {!ticketsQuery.isLoading && (ticketsQuery.data?.length ?? 0) === 0 ? (
                  <tr>
                    <td className="px-4 py-6 text-zinc-500" colSpan={6}>
                      대기 중인 티켓이 없습니다.
                    </td>
                  </tr>
                ) : null}
                {ticketsQuery.data?.map((ticket) => (
                  <tr key={ticket.ticketId}>
                    <td className="px-4 py-3">{ticket.categoryName}</td>
                    <td className="px-4 py-3">{ticket.customerName}</td>
                    <td className="px-4 py-3">
                      <Link
                        className="font-medium text-teal-700 hover:underline"
                        href={`/agent/tickets/${ticket.ticketId}` as Route}
                      >
                        {ticket.subject}
                      </Link>
                      <div className="text-xs text-zinc-500">{ticket.ticketNo}</div>
                    </td>
                    <td className="px-4 py-3">{formatDateTime(ticket.createdAt)}</td>
                    <td className="px-4 py-3">대기</td>
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
        </div>
      </div>
    </PageShell>
  );
}
