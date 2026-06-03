"use client";

import type { Route } from "next";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { AlertBanner } from "@/components/alert-banner";
import { PageShell } from "@/components/page-shell";
import { fetchCustomerTicket, fetchCustomerTicketMessages } from "@/lib/api/customer";
import { ApiError } from "@/lib/api/client";
import { formatDateTime, formatSenderType, formatTicketStatus } from "@/lib/format";
import { useRequireAuth } from "@/lib/use-require-auth";

export default function CustomerTicketDetailPage() {
  const { status: authStatus } = useRequireAuth();
  const params = useParams<{ ticketId: string }>();
  const ticketId = Number(params.ticketId);

  const detailQuery = useQuery({
    queryKey: ["customer-ticket-detail", ticketId],
    queryFn: () => fetchCustomerTicket(ticketId),
    enabled: authStatus === "allowed" && Number.isFinite(ticketId)
  });

  const messagesQuery = useQuery({
    queryKey: ["ticket-messages", ticketId],
    queryFn: () => fetchCustomerTicketMessages(ticketId),
    enabled: authStatus === "allowed" && Number.isFinite(ticketId),
    refetchInterval: detailQuery.data?.status === "CLOSED" ? false : 10_000
  });

  if (authStatus !== "allowed") {
    return null;
  }

  const detail = detailQuery.data;
  const isClosed = detail?.status === "CLOSED" || detail?.status === "RESOLVED";
  const errorMessage =
    detailQuery.error instanceof ApiError
      ? detailQuery.error.message
      : detailQuery.isError
        ? "문의 상세를 불러오지 못했습니다."
        : null;

  return (
    <PageShell
      title="문의 상세"
      description={detail ? `${detail.ticketNo} · ${formatTicketStatus(detail.status)}` : undefined}
    >
      <div className="mb-4">
        <Link className="text-sm text-teal-700 hover:underline" href={"/customer/tickets" as Route}>
          내 문의 목록
        </Link>
      </div>

      <div className="space-y-4">
        {errorMessage ? <AlertBanner message={errorMessage} variant="error" /> : null}
        {detailQuery.isLoading ? <p className="text-sm text-zinc-500">문의 정보를 불러오는 중...</p> : null}

        {detail ? (
          <>
            <section className="rounded-lg border border-zinc-200 bg-white p-5">
              <div className="mb-4 flex flex-wrap items-start justify-between gap-3">
                <div>
                  <h2 className="text-lg font-semibold text-zinc-900">{detail.subject}</h2>
                  <p className="mt-1 text-sm text-zinc-500">
                    {detail.categoryName} · {formatDateTime(detail.createdAt)}
                  </p>
                </div>
                <span className={`rounded-full px-3 py-1 text-sm font-medium ${isClosed ? "bg-zinc-100 text-zinc-700" : "bg-teal-50 text-teal-700"}`}>
                  {isClosed ? "상담 종료" : "상담 진행중"}
                </span>
              </div>
              <dl className="grid gap-3 text-sm sm:grid-cols-2">
                <InfoItem label="상태" value={formatTicketStatus(detail.status)} />
                <InfoItem label="고객명" value={detail.customerName} />
                <InfoItem label="연락처" value={detail.customerPhone || "-"} />
                <InfoItem label="이메일" value={detail.customerEmail || "-"} />
              </dl>
            </section>

            <section className="rounded-lg border border-zinc-200 bg-white p-5">
              <h2 className="mb-2 text-base font-semibold">내가 남긴 문의</h2>
              <p className="whitespace-pre-wrap text-sm text-zinc-700">{detail.inquiryContent || "문의 내용이 없습니다."}</p>
            </section>

            <section className="rounded-lg border border-zinc-200 bg-white p-5">
              <h2 className="mb-3 text-base font-semibold">상담 이력</h2>
              {messagesQuery.isLoading ? <p className="text-sm text-zinc-500">상담 이력을 불러오는 중...</p> : null}
              {messagesQuery.isError ? <AlertBanner message="상담 이력을 불러오지 못했습니다." variant="error" /> : null}
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
                {detail.closedAt ? <p className="mt-2 text-xs text-teal-800">종료 시간: {formatDateTime(detail.closedAt)}</p> : null}
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
