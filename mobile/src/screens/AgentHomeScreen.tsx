import { useRouter } from "expo-router";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Screen } from "@/components/Screen";
import { msg } from "@/messages";
import { clearAccessToken } from "@/storage/authStorage";

export function AgentHomeScreen() {
  const router = useRouter();

  async function logout() {
    await clearAccessToken();
    router.replace("/auth/login");
  }

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>{msg.ui("agent.homeTitle")}</Text>
        <Text style={styles.description}>{msg.ui("agent.homeDescription")}</Text>
        <TouchableOpacity onPress={() => router.push("/account")} style={styles.linkButton}>
          <Text style={styles.linkButtonText}>{msg.ui("nav.account")}</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={logout} style={styles.button}>
          <Text style={styles.buttonText}>{msg.ui("common.logout")}</Text>
        </TouchableOpacity>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  card: { gap: 12, backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 12, padding: 16 },
  title: { fontSize: 22, fontWeight: "900", color: "#09090b" },
  description: { color: "#52525b", fontSize: 14, lineHeight: 20 },
  linkButton: {
    borderWidth: 1,
    borderColor: "#d4d4d8",
    borderRadius: 8,
    minHeight: 44,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 8
  },
  linkButtonText: { color: "#0f766e", fontWeight: "800" },
  button: { backgroundColor: "#09090b", borderRadius: 8, minHeight: 46, alignItems: "center", justifyContent: "center", marginTop: 8 },
  buttonText: { color: "#fff", fontWeight: "800" }
});
