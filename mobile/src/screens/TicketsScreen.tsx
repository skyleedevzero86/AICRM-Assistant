import { useCallback, useState } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useFocusEffect, useRouter } from "expo-router";
import { Screen } from "@/components/Screen";
import { EmptyState } from "@/components/EmptyState";
import { ErrorMessage } from "@/components/ErrorMessage";
import { LoadingView } from "@/components/LoadingView";
import { TicketCard } from "@/components/TicketCard";
import { loadAndRefreshTickets } from "@/storage/ticketStorage";
import type { StoredTicket } from "@/storage/ticketStorage";
import { refreshStoredTickets } from "@/utils/ticket-sync";

export function TicketsScreen() {
  const router = useRouter();
  const [tickets, setTickets] = useState<StoredTicket[]>([]);
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function loadTickets() {
    setLoading(true);
    setErrorMessage(null);
    try {
      const refreshed = await loadAndRefreshTickets(refreshStoredTickets);
      setTickets(refreshed);
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "문의 목록을 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  }

  useFocusEffect(
    useCallback(() => {
      void loadTickets();
    }, [])
  );

  return (
    <Screen>
      <View style={styles.headerRow}>
        <View>
          <Text style={styles.title}>내 문의</Text>
          <Text style={styles.description}>이 기기에서 접수한 문의 내역입니다.</Text>
        </View>
        <TouchableOpacity disabled={loading} onPress={loadTickets} style={styles.refreshButton}>
          <Text style={styles.refreshText}>새로고침</Text>
        </TouchableOpacity>
      </View>

      {loading ? <LoadingView /> : null}
      {errorMessage ? <ErrorMessage message={errorMessage} /> : null}
      {!loading
        ? tickets.map((ticket) => (
            <TicketCard
              key={ticket.ticketId}
              onPress={() => router.push(`/tickets/${ticket.ticketId}`)}
              ticket={ticket}
            />
          ))
        : null}
      {!loading && !errorMessage && tickets.length === 0 ? (
        <EmptyState
          description="문의 탭에서 접수하면 이 목록에 표시됩니다."
          title="표시할 문의가 없습니다"
        />
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
  }
});
