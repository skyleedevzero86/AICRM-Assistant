import { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { fetchCustomerTickets } from "@/api/customer";
import type { CustomerTicketSummary } from "@/api/types";
import { Screen } from "@/components/Screen";
import { TicketCard } from "@/components/TicketCard";

export function TicketsScreen() {
  const [tickets, setTickets] = useState<CustomerTicketSummary[]>([]);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function loadTickets() {
    setLoading(true);
    setErrorMessage(null);
    try {
      setTickets(await fetchCustomerTickets());
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "문의 목록을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadTickets();
  }, []);

  return (
    <Screen>
      <View style={styles.headerRow}>
        <View>
          <Text style={styles.title}>내 문의</Text>
          <Text style={styles.description}>문의 상태와 상담 이력을 확인하세요.</Text>
        </View>
        <TouchableOpacity onPress={loadTickets} style={styles.refreshButton}>
          <Text style={styles.refreshText}>새로고침</Text>
        </TouchableOpacity>
      </View>

      {loading ? <ActivityIndicator color="#0f766e" /> : null}
      {errorMessage ? <Text style={styles.error}>{errorMessage}</Text> : null}
      {tickets.map((ticket) => (
        <TicketCard key={ticket.ticketId} ticket={ticket} />
      ))}
      {!loading && tickets.length === 0 ? (
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>표시할 문의가 없습니다.</Text>
        </View>
      ) : null}
    </Screen>
  );
}

const styles = StyleSheet.create({
  headerRow: {
    alignItems: "flex-start",
    flexDirection: "row",
    gap: 12,
    justifyContent: "space-between"
  },
  title: {
    color: "#09090b",
    fontSize: 22,
    fontWeight: "900"
  },
  description: {
    color: "#71717a",
    fontSize: 14,
    lineHeight: 22,
    marginTop: 6
  },
  refreshButton: {
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 8,
    borderWidth: 1,
    paddingHorizontal: 12,
    paddingVertical: 9
  },
  refreshText: {
    color: "#27272a",
    fontSize: 13,
    fontWeight: "800"
  },
  error: {
    backgroundColor: "#fef2f2",
    borderRadius: 8,
    color: "#b91c1c",
    padding: 12
  },
  emptyCard: {
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 10,
    borderStyle: "dashed",
    borderWidth: 1,
    padding: 18
  },
  emptyText: {
    color: "#52525b",
    fontSize: 14
  }
});
