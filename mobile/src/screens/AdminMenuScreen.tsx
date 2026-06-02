import { Link } from "expo-router";
import { StyleSheet, Text, View } from "react-native";
import { Screen } from "@/components/Screen";
import { msg } from "@/messages";

export function AdminMenuScreen() {
  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>{msg.ui("admin.menuTitle")}</Text>
        <Link href="/admin/agents" style={styles.menuLink}>
          {msg.ui("admin.agentsLink")}
        </Link>
        <Link href="/admin/customers" style={styles.menuLink}>
          {msg.ui("admin.customersLink")}
        </Link>
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
