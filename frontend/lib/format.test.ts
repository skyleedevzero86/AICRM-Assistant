import { describe, expect, it } from "vitest";
import { formatSenderType, formatTicketStatus } from "./format";

describe("formatTicketStatus", () => {
  it("returns Korean label for ticket status", () => {
    const status = "WAITING";

    const label = formatTicketStatus(status);

    expect(label).toBe("대기");
  });
});

describe("formatSenderType", () => {
  it("returns Korean label for sender type", () => {
    const senderType = "AGENT";

    const label = formatSenderType(senderType);

    expect(label).toBe("상담원");
  });
});
