import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import { msg } from "@/messages";

export function LoadingView({ label = msg.ui("common.loading") }: { label?: string }) {
  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color="#0f766e" />
      <Text style={styles.label}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { alignItems: "center", justifyContent: "center", gap: 12, paddingVertical: 24 },
  label: { color: "#52525b", fontSize: 14 }
});
