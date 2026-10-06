import type { CreateCustomerInquiryResponse } from "@/lib/api/types";

export type RecentInquiryRecord = CreateCustomerInquiryResponse & {
  title: string;
  customerName: string;
  submittedAt: string;
};

const STORAGE_KEY = "aicrm-recent-inquiries";
const MAX_ITEMS = 10;

function canUseSessionStorage(): boolean {
  return typeof sessionStorage !== "undefined";
}

export function loadRecentInquiries(): RecentInquiryRecord[] {
  if (!canUseSessionStorage()) return [];
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw) as RecentInquiryRecord[];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export function saveRecentInquiry(record: RecentInquiryRecord): RecentInquiryRecord[] {
  const next = [record, ...loadRecentInquiries().filter((item) => item.ticketId !== record.ticketId)].slice(
    0,
    MAX_ITEMS
  );
  if (canUseSessionStorage()) {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  }
  return next;
}
