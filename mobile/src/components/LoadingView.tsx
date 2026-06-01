import { ActivityIndicator, StyleSheet, Text, View } from "react-native";

export function LoadingView({ label = "불러오는 중..." }: { label?: string }) {
  return (
    <View style={styles.container}>
      <ActivityIndicator color="#0f766e" size="large" />
      <Text style={styles.label}>{label}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
    gap: 12,
    paddingVertical: 24
  },
  label: {
    color: "#52525b",
    fontSize: 14
  }
});
