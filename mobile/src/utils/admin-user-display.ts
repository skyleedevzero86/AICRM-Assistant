import { StyleSheet, type TextStyle } from "react-native";
import { resolveAdminUserDisplayState, type YnFlag } from "../../../shared/admin-user-display";

export { resolveAdminUserDisplayState };
export type { YnFlag };

export function adminUserFieldStyle(withdrawnYn: YnFlag, suspendedYn: YnFlag): TextStyle {
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
  normal: { color: "#09090b" },
  withdrawn: { color: "#dc2626", textDecorationLine: "line-through" },
  suspended: { color: "#7e22ce" }
});
