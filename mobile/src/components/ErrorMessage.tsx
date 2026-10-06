import { StyleSheet, Text, View } from "react-native";

export function ErrorMessage({ message }: { message: string }) {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>{message}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: "#fef2f2",
    borderRadius: 8,
    padding: 12
  },
  text: {
    color: "#b91c1c",
    fontSize: 14,
    lineHeight: 20
  }
});
