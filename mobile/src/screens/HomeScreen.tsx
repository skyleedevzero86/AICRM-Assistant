import { useCallback, useState } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useFocusEffect, useRouter } from "expo-router";
import { Screen } from "@/components/Screen";
import { msg } from "@/messages";
import { EmptyState } from "@/components/EmptyState";
import { TicketCard } from "@/components/TicketCard";
import type { CustomerTicketSummary } from "@/api/types";
import { clearAccessToken } from "@/storage/authStorage";
import { clearStoredTickets } from "@/storage/ticketStorage";
import { loadCustomerTickets } from "@/utils/ticket-sync";

export function HomeScreen() {
  const router = useRouter();
  const [tickets, setTickets] = useState<CustomerTicketSummary[]>([]);

  useFocusEffect(
    useCallback(() => {
      let active = true;
      loadCustomerTickets().then((next) => {
        if (active) setTickets(next.slice(0, 3));
      });
      return () => {
        active = false;
      };
    }, [])
  );

  async function logout() {
    await clearAccessToken();
    await clearStoredTickets();
    router.replace("/auth/login");
  }

  return (
    <Screen>
      <View style={styles.hero}>
        <Text style={styles.heroEyebrow}>안녕하세요, 고객님</Text>
        <Text style={styles.heroTitle}>무엇을 도와드릴까요?</Text>
        <TouchableOpacity onPress={() => router.push("/(tabs)/inquiry")} style={styles.heroButton}>
          <Text style={styles.heroButtonText}>빠른 문의하기</Text>
        </TouchableOpacity>
        <View style={styles.heroActions}>
          <TouchableOpacity onPress={() => router.push("/(tabs)/account")} style={styles.secondaryHeroButton}>
            <Text style={styles.secondaryHeroButtonText}>{msg.ui("nav.account")}</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => void logout()} style={styles.secondaryHeroButton}>
            <Text style={styles.secondaryHeroButtonText}>{msg.ui("common.logout")}</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>진행 중 문의</Text>
        {tickets.length > 0 ? (
          <View style={styles.list}>
            {tickets.map((ticket) => (
              <TicketCard
                key={ticket.ticketId}
                onPress={() => router.push(`/tickets/${ticket.ticketId}`)}
                ticket={ticket}
              />
            ))}
          </View>
        ) : (
          <EmptyState
            description="문의 탭에서 접수하면 이곳에서 최근 문의를 확인할 수 있습니다."
            title="접수한 문의가 없습니다"
          />
        )}
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>빠른 메뉴</Text>
        <View style={styles.quickGrid}>
          <TouchableOpacity onPress={() => router.push("/(tabs)/inquiry")} style={styles.quickItem}>
            <Text style={styles.quickText}>문의하기</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => router.push("/(tabs)/tickets")} style={styles.quickItem}>
            <Text style={styles.quickText}>내 문의</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  hero: {
    backgroundColor: "#09090b",
    borderRadius: 12,
    padding: 20
  },
  heroEyebrow: {
    color: "#d4d4d8",
    fontSize: 14
  },
  heroTitle: {
    color: "#ffffff",
    fontSize: 26,
    fontWeight: "900",
    marginTop: 8
  },
  heroButton: {
    alignItems: "center",
    alignSelf: "flex-start",
    backgroundColor: "#ffffff",
    borderRadius: 8,
    marginTop: 20,
    minHeight: 44,
    paddingHorizontal: 16,
    justifyContent: "center"
  },
  heroButtonText: {
    color: "#09090b",
    fontSize: 14,
    fontWeight: "800"
  },
  heroActions: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginTop: 12
  },
  secondaryHeroButton: {
    alignItems: "center",
    backgroundColor: "#27272a",
    borderRadius: 8,
    minHeight: 36,
    paddingHorizontal: 12,
    justifyContent: "center"
  },
  secondaryHeroButtonText: {
    color: "#fafafa",
    fontSize: 13,
    fontWeight: "800"
  },
  section: {
    gap: 10
  },
  sectionTitle: {
    color: "#09090b",
    fontSize: 17,
    fontWeight: "900"
  },
  list: {
    gap: 10
  },
  quickGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8
  },
  quickItem: {
    alignItems: "center",
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 10,
    borderWidth: 1,
    flexBasis: "48%",
    minHeight: 72,
    justifyContent: "center"
  },
  quickText: {
    color: "#18181b",
    fontSize: 14,
    fontWeight: "800"
  }
});
