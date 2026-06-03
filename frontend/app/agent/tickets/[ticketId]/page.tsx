"use client";

import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Route } from "next";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useState } from "react";
import { AlertBanner } from "@/components/alert-banner";
import { FormField } from "@/components/form-field";
import { PageShell } from "@/components/page-shell";
import { TicketAttachmentPanel } from "@/components/ticket-attachment-panel";
import {
  acceptTicket,
  closeTicket,
  fetchAgentTicketDetail,
  fetchTicketMessages,
  saveAgentMessage
} from "@/lib/api/agent";
import { ApiError } from "@/lib/api/client";
import { formatDateTime, formatSenderType, formatTicketStatus } from "@/lib/format";
import { msg } from "@/lib/messages";

const inputClassName =
  "w-full rounded-md border border-zinc-300 bg-white px-3 py-2 text-sm outline-none focus:border-teal-600 focus:ring-1 focus:ring-teal-600";

export default function AgentTicketDetailPage() {
  const params = useParams<{ ticketId: string }>();
  const queryClient = useQueryClient();
  const ticketId = Number(params.ticketId);

  const [reply, setReply] = useState("");
  const [resolution, setResolution] = useState("");
  const [actionMessage, setActionMessage] = useState<string | null>(null);
  const [actionError, setActionError] = useState<string | null>(null);

  const detailQuery = useQuery({
    queryKey: ["agent-ticket-detail", ticketId],
    queryFn: () => fetchAgentTicketDetail(ticketId),
    enabled: Number.isFinite(ticketId)
  });

  const messagesQuery = useQuery({
    queryKey: ["ticket-messages", ticketId],
    queryFn: () => fetchTicketMessages(ticketId),
    enabled: Number.isFinite(ticketId),
    refetchInterval: detailQuery.data?.status === "CLOSED" ? false : 10_000
  });

  const acceptMutation = useMutation({
    mutationFn: () => acceptTicket(ticketId),
    onSuccess: () => {
      setActionError(null);
      setActionMessage("티켓을 수락했습니다.");
      queryClient.invalidateQueries({ queryKey: ["agent-ticket-detail", ticketId] });
      queryClient.invalidateQueries({ queryKey: ["waiting-tickets"] });
      queryClient.invalidateQueries({ queryKey: ["agent-tickets"] });
    },
    onError: (error) => {
      setActionMessage(null);
      setActionError(error instanceof ApiError ? error.message : "티켓 수락에 실패했습니다.");
    }
  });

  const messageMutation = useMutation({
    mutationFn: (content: string) => saveAgentMessage(ticketId, { content, messageType: "TEXT" }),
    onSuccess: () => {
      setReply("");
      setActionError(null);
      setActionMessage("메시지를 전송했습니다.");
      queryClient.invalidateQueries({ queryKey: ["ticket-messages", ticketId] });
    },
    onError: (error) => {
      setActionMessage(null);
      setActionError(error instanceof ApiError ? error.message : "메시지 전송에 실패했습니다.");
    }
  });

  const closeMutation = useMutation({
    mutationFn: (resolutionText: string) => closeTicket(ticketId, { resolution: resolutionText }),
    onSuccess: () => {
      setActionError(null);
      setActionMessage("상담을 종료했습니다.");
      queryClient.invalidateQueries({ queryKey: ["agent-ticket-detail", ticketId] });
      queryClient.invalidateQueries({ queryKey: ["agent-tickets"] });
    },
    onError: (error) => {
      setActionMessage(null);
      setActionError(error instanceof ApiError ? error.message : "상담 종료에 실패했습니다.");
    }
  });

  const detail = detailQuery.data;
  const status = detail?.status;
  const canAccept = status === "WAITING" || status === "ASSIGNED";
  const canReply = status === "IN_PROGRESS" || status === "RESOLVED" || status === "ASSIGNED";
  const canClose = status === "IN_PROGRESS" || status === "RESOLVED";

  function handleSendMessage(event: React.FormEvent) {
    event.preventDefault();
    const trimmed = reply.trim();
    if (!trimmed) {
      setActionError("답변 내용을 입력해 주세요.");
      return;
    }
    setActionError(null);
    messageMutation.mutate(trimmed);
  }

  function handleCloseTicket() {
    const trimmed = resolution.trim();
    if (!trimmed) {
      setActionError("처리 결과를 입력해 주세요.");
      return;
    }
    setActionError(null);
    closeMutation.mutate(trimmed);
  }

  const loadError =
    detailQuery.error instanceof ApiError
      ? detailQuery.error.message
      : detailQuery.isError
        ? "티켓 정보를 불러오지 못했습니다."
        : null;

  return (
    <PageShell
      title="상담 상세"
      description={detail ? `${detail.ticketNo} · ${formatTicketStatus(detail.status)}` : undefined}
    >
      <div className="mb-4">
        <Link className="text-sm text-teal-700 hover:underline" href={"/agent/tickets" as Route}>
          상담원 티켓 목록
        </Link>
      </div>

      <div className="space-y-4">
        {actionMessage ? <AlertBanner message={actionMessage} variant="success" /> : null}
        {actionError ? <AlertBanner message={actionError} variant="error" /> : null}
        {loadError ? <AlertBanner message={loadError} variant="error" /> : null}
        {detailQuery.isLoading ? <p className="text-sm text-zinc-500">티켓 정보를 불러오는 중...</p> : null}

        {detail ? (
          <>
            <section className="rounded-lg border border-zinc-200 bg-white p-6">
              <h2 className="mb-4 text-base font-semibold">고객 정보</h2>
              <dl className="grid gap-3 text-sm sm:grid-cols-2">
                <InfoItem label="고객명" value={detail.customerName} />
                <InfoItem label="연락처" value={detail.customerPhone || "-"} />
                <InfoItem label="이메일" value={detail.customerEmail || "-"} />
                <InfoItem label="문의 유형" value={detail.categoryName || "-"} />
                <InfoItem label="접수 시간" value={formatDateTime(detail.createdAt)} />
                <InfoItem label="상태" value={formatTicketStatus(detail.status)} />
              </dl>
              {canAccept ? (
                <button
                  className="mt-4 rounded-md bg-zinc-900 px-4 py-2 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-50"
                  disabled={acceptMutation.isPending}
                  onClick={() => acceptMutation.mutate()}
                  type="button"
                >
                  {acceptMutation.isPending ? "수락 중..." : "티켓 수락"}
                </button>
              ) : null}
            </section>

            <section className="rounded-lg border border-zinc-200 bg-white p-6">
              <h2 className="mb-2 text-base font-semibold">문의 내용</h2>
              <p className="mb-2 text-sm font-medium">{detail.subject}</p>
              <p className="whitespace-pre-wrap text-sm text-zinc-700">{detail.inquiryContent || "문의 내용이 없습니다."}</p>
            </section>

            <TicketAttachmentPanel
              canUpload={canReply}
              downloadErrorMessage={msg.ui("agent.downloadAttachmentFailed")}
              loadErrorMessage={msg.ui("agent.loadAttachmentsFailed")}
              role="AGENT"
              ticketId={ticketId}
              uploadErrorMessage={msg.ui("agent.uploadAttachmentFailed")}
            />

            <section className="rounded-lg border border-zinc-200 bg-white p-6">
              <h2 className="mb-4 text-base font-semibold">상담 메시지 이력</h2>
              {messagesQuery.isLoading ? <p className="text-sm text-zinc-500">메시지를 불러오는 중...</p> : null}
              {messagesQuery.isError ? <AlertBanner message="메시지 목록을 불러오지 못했습니다." variant="error" /> : null}
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
              {(messagesQuery.data?.length ?? 0) === 0 && !messagesQuery.isLoading ? (
                <p className="text-sm text-zinc-500">등록된 메시지가 없습니다.</p>
              ) : null}
            </section>

            {canReply ? (
              <section className="rounded-lg border border-zinc-200 bg-white p-6">
                <form className="space-y-4" onSubmit={handleSendMessage}>
                  <FormField htmlFor="reply" label="답변 입력" required>
                    <textarea
                      className={`${inputClassName} min-h-[120px] resize-y`}
                      id="reply"
                      onChange={(event) => setReply(event.target.value)}
                      value={reply}
                    />
                  </FormField>
                  <button
                    className="rounded-md bg-teal-700 px-4 py-2 text-sm font-medium text-white hover:bg-teal-800 disabled:opacity-50"
                    disabled={messageMutation.isPending}
                    type="submit"
                  >
                    {messageMutation.isPending ? "전송 중..." : "메시지 전송"}
                  </button>
                </form>
              </section>
            ) : null}

            {canClose ? (
              <section className="rounded-lg border border-zinc-200 bg-white p-6">
                <h2 className="mb-4 text-base font-semibold">상담 종료</h2>
                <FormField htmlFor="resolution" label="처리 결과" required>
                  <textarea
                    className={`${inputClassName} min-h-[100px] resize-y`}
                    id="resolution"
                    onChange={(event) => setResolution(event.target.value)}
                    value={resolution}
                  />
                </FormField>
                <button
                  className="mt-4 rounded-md border border-red-300 bg-white px-4 py-2 text-sm font-medium text-red-700 hover:bg-red-50 disabled:opacity-50"
                  disabled={closeMutation.isPending}
                  onClick={handleCloseTicket}
                  type="button"
                >
                  {closeMutation.isPending ? "종료 중..." : "상담 종료"}
                </button>
              </section>
            ) : null}

            {detail.resolution ? (
              <section className="rounded-lg border border-teal-200 bg-teal-50 p-6">
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
