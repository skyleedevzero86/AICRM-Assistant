import { Link } from "expo-router";
import { StyleSheet, Text, View } from "react-native";
import { Screen } from "@/components/Screen";

export function AdminMenuScreen() {
  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>관리자 메뉴</Text>
        <Link href="/admin/agents" style={styles.menuLink}>상담사 회원 목록</Link>
        <Link href="/admin/customers" style={styles.menuLink}>고객 회원 목록</Link>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  card: { gap: 12, backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 12, padding: 16 },
  title: { fontSize: 22, fontWeight: "900", color: "#09090b" },
  menuLink: {
    borderWidth: 1,
    borderColor: "#d4d4d8",
    borderRadius: 8,
    color: "#0f766e",
    fontWeight: "800",
    paddingHorizontal: 12,
    paddingVertical: 10
  }
});
