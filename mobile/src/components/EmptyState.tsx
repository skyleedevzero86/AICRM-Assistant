import { StyleSheet, Text, View } from "react-native";

export function EmptyState({ title, description }: { title: string; description?: string }) {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>{title}</Text>
      {description ? <Text style={styles.description}>{description}</Text> : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 10,
    borderStyle: "dashed",
    borderWidth: 1,
    gap: 6,
    padding: 18
  },
  title: {
    color: "#27272a",
    fontSize: 15,
    fontWeight: "800"
  },
  description: {
    color: "#52525b",
    fontSize: 14,
    lineHeight: 20
  }
});
