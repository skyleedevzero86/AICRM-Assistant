import { StyleSheet, Text, View } from "react-native";
import { Screen } from "@/components/Screen";

export function ProfileScreen() {
  return (
    <Screen>
      <Text style={styles.title}>마이</Text>
      <View style={styles.card}>
        <Row label="내 정보" value="준비 중" />
        <Row label="알림 설정" value="준비 중" />
        <Row label="개인정보 처리 안내" value="준비 중" />
      </View>
    </Screen>
  );
}

function Row({ label, value }: { label: string; value: string }) {
  return (
    <View style={styles.row}>
      <Text style={styles.label}>{label}</Text>
      <Text style={styles.value}>{value}</Text>
    </View>
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
    borderWidth: 1
  },
  row: {
    alignItems: "center",
    borderBottomColor: "#f4f4f5",
    borderBottomWidth: 1,
    flexDirection: "row",
    justifyContent: "space-between",
    padding: 16
  },
  label: {
    color: "#27272a",
    fontSize: 15,
    fontWeight: "800"
  },
  value: {
    color: "#a1a1aa",
    fontSize: 14,
    fontWeight: "700"
  }
});
