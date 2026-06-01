import { StyleSheet, Text, View } from "react-native";
import { Screen } from "@/components/Screen";

export function ChatScreen() {
  return (
    <Screen>
      <Text style={styles.title}>상담</Text>
      <View style={styles.card}>
        <Text style={styles.cardTitle}>채팅 상담 준비 중</Text>
        <Text style={styles.cardText}>
          현재는 문의 접수와 내 문의 확인을 먼저 제공합니다. 실시간 채팅은 WebSocket 연동 단계에서 추가합니다.
        </Text>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  title: {
    color: "#09090b",
    fontSize: 22,
    fontWeight: "900"
  },
  card: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 12,
    borderWidth: 1,
    padding: 18
  },
  cardTitle: {
    color: "#09090b",
    fontSize: 17,
    fontWeight: "900"
  },
  cardText: {
    color: "#52525b",
    fontSize: 14,
    lineHeight: 22,
    marginTop: 8
  }
});
