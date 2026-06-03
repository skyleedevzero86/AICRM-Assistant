export type YnFlag = "Y" | "N";

export type AdminUserDisplayState = "normal" | "withdrawn" | "suspended";

export function normalizeYnFlag(value: string | null | undefined): YnFlag {
  return value?.trim().toUpperCase() === "Y" ? "Y" : "N";
}

export function resolveAdminUserDisplayState(
  withdrawnYn: string | null | undefined,
  suspendedYn: string | null | undefined
): AdminUserDisplayState {
  if (normalizeYnFlag(withdrawnYn) === "Y") {
    return "withdrawn";
  }
  if (normalizeYnFlag(suspendedYn) === "Y") {
    return "suspended";
  }
  return "normal";
}
