import { StyleSheet, Text, View } from "react-native";
import type { CustomerTicketSummary } from "@/api/types";
import { formatDateTime, formatTicketStatus } from "@/utils/format";

export function TicketCard({ ticket }: { ticket: CustomerTicketSummary }) {
  return (
    <View style={styles.card}>
      <View style={styles.row}>
        <View style={styles.titleBlock}>
          <Text style={styles.ticketNo}>{ticket.ticketNo}</Text>
          <Text numberOfLines={1} style={styles.title}>
            {ticket.subject}
          </Text>
        </View>
        <Text style={styles.badge}>{formatTicketStatus(ticket.status)}</Text>
      </View>
      <Text style={styles.category}>{ticket.categoryName}</Text>
      <Text style={styles.date}>{formatDateTime(ticket.createdAt)}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 10,
    borderWidth: 1,
    gap: 10,
    padding: 16
  },
  row: {
    alignItems: "flex-start",
    flexDirection: "row",
    gap: 12,
    justifyContent: "space-between"
  },
  titleBlock: {
    flex: 1
  },
  ticketNo: {
    color: "#71717a",
    fontSize: 12,
    fontWeight: "700"
  },
  title: {
    color: "#09090b",
    fontSize: 16,
    fontWeight: "800",
    marginTop: 4
  },
  badge: {
    backgroundColor: "#ecfdf5",
    borderRadius: 999,
    color: "#0f766e",
    fontSize: 12,
    fontWeight: "800",
    overflow: "hidden",
    paddingHorizontal: 10,
    paddingVertical: 5
  },
  category: {
    color: "#52525b",
    fontSize: 14
  },
  date: {
    color: "#a1a1aa",
    fontSize: 12
  }
});
