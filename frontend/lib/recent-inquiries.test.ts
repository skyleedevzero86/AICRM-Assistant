import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { loadRecentInquiries, saveRecentInquiry } from "./recent-inquiries";

function mockSessionStorage() {
  const store = new Map<string, string>();
  vi.stubGlobal("sessionStorage", {
    getItem: (key: string) => store.get(key) ?? null,
    setItem: (key: string, value: string) => {
      store.set(key, value);
    },
    clear: () => {
      store.clear();
    }
  });
}

const sample = {
  ticketId: 1,
  ticketNo: "T-20260101-0001",
  status: "WAITING" as const,
  title: "배송 문의",
  customerName: "홍길동",
  submittedAt: "2026-06-01T00:00:00.000Z"
};

describe("recent-inquiries", () => {
  beforeEach(() => {
    mockSessionStorage();
  });

  afterEach(() => {
    sessionStorage.clear();
    vi.unstubAllGlobals();
  });

  it("saves and loads recent inquiries in session order", () => {
    saveRecentInquiry(sample);
    saveRecentInquiry({ ...sample, ticketId: 2, ticketNo: "T-20260101-0002", title: "환불 문의" });

    expect(loadRecentInquiries()).toHaveLength(2);
    expect(loadRecentInquiries()[0]?.ticketId).toBe(2);
  });

  it("deduplicates by ticket id", () => {
    saveRecentInquiry(sample);
    saveRecentInquiry({ ...sample, title: "수정된 제목" });

    expect(loadRecentInquiries()).toHaveLength(1);
    expect(loadRecentInquiries()[0]?.title).toBe("수정된 제목");
  });
});
