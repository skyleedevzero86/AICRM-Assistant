import { ReactNode } from "react";
import { ScrollView, StyleSheet } from "react-native";

export function Screen({ children }: { children: ReactNode }) {
  return <ScrollView contentContainerStyle={styles.content}>{children}</ScrollView>;
}

const styles = StyleSheet.create({
  content: {
    gap: 16,
    padding: 20,
    paddingBottom: 28
  }
});
