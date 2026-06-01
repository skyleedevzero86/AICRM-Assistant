import { afterEach, describe, expect, it, vi } from "vitest";
import { ApiError, apiRequest } from "./client";

describe("apiRequest", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("returns data when API responds with success", async () => {
    const responseBody = { success: true, data: { ticketId: 10 }, error: null };
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: true,
        json: async () => responseBody
      })
    );

    const data = await apiRequest<{ ticketId: number }>("/api/agent/tickets/10");

    expect(data.ticketId).toBe(10);
  });

  it("throws ApiError when API responds with failure", async () => {
    const responseBody = {
      success: false,
      data: null,
      error: { code: "TICKET_NOT_FOUND", message: "티켓을 찾을 수 없습니다." }
    };
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue({
        ok: 404,
        json: async () => responseBody
      })
    );

    await expect(apiRequest("/api/agent/tickets/999")).rejects.toThrow(ApiError);
    await expect(apiRequest("/api/agent/tickets/999")).rejects.toThrow("티켓을 찾을 수 없습니다.");
  });
});
