import type { SenderType, TicketStatus } from "@/api/types";

const ticketStatusLabels: Record<TicketStatus, string> = {
  WAITING: "상담 대기",
  ASSIGNED: "배정됨",
  IN_PROGRESS: "상담 중",
  RESOLVED: "해결됨",
  CLOSED: "종료",
  ESCALATED: "이관됨"
};

const senderLabels: Record<SenderType, string> = {
  CUSTOMER: "고객",
  AGENT: "상담원",
  AI: "AI",
  SYSTEM: "시스템"
};

export function formatTicketStatus(status: TicketStatus): string {
  return ticketStatusLabels[status] ?? status;
}

export function formatSenderType(senderType: SenderType): string {
  return senderLabels[senderType] ?? senderType;
}

export function formatDateTime(iso: string): string {
  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false
  }).format(new Date(iso));
}
