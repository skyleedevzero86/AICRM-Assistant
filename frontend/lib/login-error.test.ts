import { describe, expect, it } from "vitest";
import { resolveLoginError } from "../../shared/messages/index";

describe("resolveLoginError", () => {
  it("maps AUTH_FAILED to password mismatch message", () => {
    expect(resolveLoginError("AUTH_FAILED", "사용할 수 없는 계정입니다. 관리자에게 문의하세요.")).toBe(
      "이메일 또는 비밀번호가 올바르지 않습니다"
    );
  });

  it("maps AGENT_APPROVAL_REQUIRED to pending approval message", () => {
    expect(resolveLoginError("AGENT_APPROVAL_REQUIRED")).toBe(
      "상담원 가입 승인 대기 중입니다. 관리자 승인 후 로그인할 수 있습니다."
    );
  });
});
