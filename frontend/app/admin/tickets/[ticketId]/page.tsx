"use client";

import type { Route } from "next";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchAdminTicket } from "@/lib/api/admin";
import { fetchTicketMessages } from "@/lib/api/agent";
import { ApiError } from "@/lib/api/client";
import { formatChannelType, formatDateTime, formatSenderType, formatTicketStatus } from "@/lib/format";

export default function AdminTicketDetailPage() {
  const params = useParams<{ ticketId: string }>();
  const ticketId = Number(params.ticketId);

  const detailQuery = useQuery({
    queryKey: ["admin-ticket-detail", ticketId],
    queryFn: () => fetchAdminTicket(ticketId),
    enabled: Number.isFinite(ticketId)
  });

  const messagesQuery = useQuery({
    queryKey: ["ticket-messages", ticketId],
    queryFn: () => fetchTicketMessages(ticketId),
    enabled: Number.isFinite(ticketId)
  });

  const detail = detailQuery.data;
  const errorMessage =
    detailQuery.error instanceof ApiError
      ? detailQuery.error.message
      : detailQuery.isError
        ? "상담 상세를 불러오지 못했습니다."
        : null;

  return (
    <PageShell
      title="상담 이력 상세"
      description={detail ? `${detail.ticketNo} · ${formatTicketStatus(detail.status)}` : undefined}
    >
      <div className="mb-4">
        <Link className="text-sm text-teal-700 hover:underline" href={"/admin/tickets" as Route}>
          상담 이력 목록
        </Link>
      </div>

      <div className="space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        {detailQuery.isLoading ? <p className="text-sm text-zinc-500">상담 상세를 불러오는 중...</p> : null}

        {detail ? (
          <>
            <section className="rounded-lg border border-zinc-200 bg-white p-5">
              <div className="mb-4 flex flex-wrap items-start justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold text-zinc-900">{detail.subject}</h2>
                  <p className="mt-1 text-sm text-zinc-500">
                    {detail.categoryName} · {formatChannelType(detail.channel)}
                  </p>
                </div>
                <span className="rounded-full bg-zinc-100 px-3 py-1 text-sm font-medium text-zinc-700">
                  {formatTicketStatus(detail.status)}
                </span>
              </div>
              <dl className="grid gap-3 text-sm sm:grid-cols-2">
                <InfoItem label="고객명" value={detail.customerName} />
                <InfoItem label="연락처" value={detail.customerPhone || "-"} />
                <InfoItem label="이메일" value={detail.customerEmail || "-"} />
                <InfoItem label="상담원" value={detail.agentId ? `#${detail.agentId}` : "미배정"} />
                <InfoItem label="접수 시간" value={formatDateTime(detail.createdAt)} />
                <InfoItem label="종료 시간" value={detail.closedAt ? formatDateTime(detail.closedAt) : "-"} />
              </dl>
            </section>

            <section className="rounded-lg border border-zinc-200 bg-white p-5">
              <h2 className="mb-2 text-base font-semibold">고객 문의 내용</h2>
              <p className="whitespace-pre-wrap text-sm text-zinc-700">{detail.inquiryContent || "문의 내용이 없습니다."}</p>
            </section>

            <section className="rounded-lg border border-zinc-200 bg-white p-5">
              <h2 className="mb-3 text-base font-semibold">상담 메시지 이력</h2>
              {messagesQuery.isLoading ? <p className="text-sm text-zinc-500">메시지를 불러오는 중...</p> : null}
              {messagesQuery.isError ? <AlertBanner message="메시지 이력을 불러오지 못했습니다." variant="error" /> : null}
              <ul className="space-y-3">
                {messagesQuery.data?.map((message) => (
                  <li className="rounded-md border border-zinc-100 bg-zinc-50 px-4 py-3 text-sm" key={message.messageId}>
                    <div className="mb-1 flex items-center justify-between gap-2 text-xs text-zinc-500">
                      <span>{formatSenderType(message.senderType)}</span>
                      <span>{formatDateTime(message.createdAt)}</span>
                    </div>
                    <p className="whitespace-pre-wrap text-zinc-800">{message.content}</p>
                  </li>
                ))}
              </ul>
            </section>

            {detail.resolution ? (
              <section className="rounded-lg border border-teal-200 bg-teal-50 p-5">
                <h2 className="mb-2 text-base font-semibold text-teal-900">처리 결과</h2>
                <p className="whitespace-pre-wrap text-sm text-teal-900">{detail.resolution}</p>
              </section>
            ) : null}
          </>
        ) : null}
      </div>
    </PageShell>
  );
}

function InfoItem({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt className="text-zinc-500">{label}</dt>
      <dd className="font-medium text-zinc-900">{value}</dd>
    </div>
  );
}
