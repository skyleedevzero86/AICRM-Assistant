import { afterEach, describe, expect, it, vi } from "vitest";
import { msg } from "../messages";
import { ApiError, AUTH_REQUIRED_CODE, apiRequest } from "./client";

vi.mock("../auth-storage", () => ({
  getAccessToken: vi.fn(() => null),
  clearAccessToken: vi.fn()
}));

describe("apiRequest", () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("does not call fetch when auth token is missing for protected API", async () => {
    const fetchMock = vi.fn();
    vi.stubGlobal("fetch", fetchMock);

    await expect(apiRequest("/api/admin/users/agents")).rejects.toMatchObject({
      code: AUTH_REQUIRED_CODE,
      message: msg.client("AUTH_REQUIRED")
    });
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it("returns data when API responds with success", async () => {
    const { getAccessToken } = await import("../auth-storage");
    vi.mocked(getAccessToken).mockReturnValue("test-token");
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
    const { getAccessToken } = await import("../auth-storage");
    vi.mocked(getAccessToken).mockReturnValue("test-token");
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
