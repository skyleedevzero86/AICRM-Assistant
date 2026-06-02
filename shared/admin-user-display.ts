export type YnFlag = "Y" | "N";

export type AdminUserDisplayState = "normal" | "withdrawn" | "suspended";

export function resolveAdminUserDisplayState(
  withdrawnYn: YnFlag,
  suspendedYn: YnFlag
): AdminUserDisplayState {
  if (withdrawnYn === "Y") {
    return "withdrawn";
  }
  if (suspendedYn === "Y") {
    return "suspended";
  }
  return "normal";
}
