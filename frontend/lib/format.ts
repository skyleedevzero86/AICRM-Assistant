import type { ChannelType, SenderType, TicketStatus } from "./api/types";

const TICKET_STATUS_LABELS: Record<TicketStatus, string> = {
  WAITING: "대기",
  ASSIGNED: "배정됨",
  IN_PROGRESS: "진행 중",
  RESOLVED: "해결됨",
  CLOSED: "종료",
  ESCALATED: "에스컬레이션"
};

const SENDER_TYPE_LABELS: Record<SenderType, string> = {
  CUSTOMER: "고객",
  AGENT: "상담원",
  AI: "AI",
  SYSTEM: "시스템"
};

const CHANNEL_TYPE_LABELS: Record<ChannelType, string> = {
  WEB_INQUIRY: "웹 문의",
  WEB_CHAT: "웹 채팅",
  PHONE: "전화",
  EMAIL: "이메일"
};

export function formatTicketStatus(status: TicketStatus): string {
  return TICKET_STATUS_LABELS[status] ?? status;
}

export function formatSenderType(senderType: SenderType): string {
  return SENDER_TYPE_LABELS[senderType] ?? senderType;
}

export function formatChannelType(channel: ChannelType): string {
  return CHANNEL_TYPE_LABELS[channel] ?? channel;
}

export function formatDateTime(iso: string): string {
  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false
  }).format(new Date(iso));
}
