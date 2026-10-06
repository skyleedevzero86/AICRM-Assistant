import { StyleSheet, Text, View } from "react-native";
import type { TicketStatus } from "@/api/types";
import { formatTicketStatus } from "@/utils/format";

const badgeStyles: Record<TicketStatus, { backgroundColor: string; color: string }> = {
  WAITING: { backgroundColor: "#fef9c3", color: "#a16207" },
  ASSIGNED: { backgroundColor: "#e0e7ff", color: "#4338ca" },
  IN_PROGRESS: { backgroundColor: "#dbeafe", color: "#1d4ed8" },
  RESOLVED: { backgroundColor: "#dcfce7", color: "#15803d" },
  CLOSED: { backgroundColor: "#f4f4f5", color: "#52525b" },
  ESCALATED: { backgroundColor: "#fee2e2", color: "#b91c1c" }
};

export function TicketStatusBadge({ status }: { status: TicketStatus }) {
  const palette = badgeStyles[status] ?? badgeStyles.WAITING;

  return (
    <View style={[styles.badge, { backgroundColor: palette.backgroundColor }]}>
      <Text style={[styles.label, { color: palette.color }]}>{formatTicketStatus(status)}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    alignSelf: "flex-start",
    borderRadius: 999,
    paddingHorizontal: 10,
    paddingVertical: 5
  },
  label: {
    fontSize: 12,
    fontWeight: "800"
  }
});
