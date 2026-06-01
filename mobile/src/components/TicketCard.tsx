import { Pressable, StyleSheet, Text, View } from "react-native";
import type { StoredTicket } from "@/storage/ticketStorage";
import { TicketStatusBadge } from "@/components/TicketStatusBadge";
import { formatDateTime } from "@/utils/format";

export function TicketCard({
  ticket,
  onPress
}: {
  ticket: StoredTicket;
  onPress?: () => void;
}) {
  return (
    <Pressable onPress={onPress} style={({ pressed }) => [styles.card, pressed && styles.pressed]}>
      <View style={styles.row}>
        <View style={styles.titleBlock}>
          <Text style={styles.ticketNo}>{ticket.ticketNo}</Text>
          <Text numberOfLines={1} style={styles.title}>
            {ticket.title}
          </Text>
        </View>
        <TicketStatusBadge status={ticket.status} />
      </View>
      {ticket.categoryName ? <Text style={styles.category}>{ticket.categoryName}</Text> : null}
      <Text style={styles.date}>{formatDateTime(ticket.createdAt)}</Text>
    </Pressable>
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
  pressed: {
    opacity: 0.92
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
  category: {
    color: "#52525b",
    fontSize: 14
  },
  date: {
    color: "#a1a1aa",
    fontSize: 12
  }
});
