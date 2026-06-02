import { StyleSheet, type TextStyle, type ViewStyle } from "react-native";
import { resolveAdminUserDisplayState } from "../../../shared/admin-user-display";

export { normalizeYnFlag, resolveAdminUserDisplayState } from "../../../shared/admin-user-display";
export type { YnFlag } from "../../../shared/admin-user-display";

export function adminUserRowStyle(withdrawnYn: string, suspendedYn: string): ViewStyle {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  if (state === "withdrawn") {
    return styles.rowWithdrawn;
  }
  if (state === "suspended") {
    return styles.rowSuspended;
  }
  return styles.rowNormal;
}

export function adminUserFieldStyle(withdrawnYn: string, suspendedYn: string): TextStyle {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  if (state === "withdrawn") {
    return styles.withdrawn;
  }
  if (state === "suspended") {
    return styles.suspended;
  }
  return styles.normal;
}

const styles = StyleSheet.create({
  rowNormal: { backgroundColor: "#fff" },
  rowWithdrawn: { backgroundColor: "#fef2f2" },
  rowSuspended: { backgroundColor: "#faf5ff" },
  normal: { color: "#09090b" },
  withdrawn: { color: "#dc2626", textDecorationLine: "line-through" },
  suspended: { color: "#7e22ce" }
});
