import { useCallback, useState } from "react";
import { useFocusEffect } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { fetchAdminAgents } from "@/api/admin";
import type { AdminAgentUser } from "@/api/types";
import { Screen } from "@/components/Screen";

export function AdminAgentsScreen() {
  const [keyword, setKeyword] = useState("");
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<AdminAgentUser[]>([]);

  useFocusEffect(
    useCallback(() => {
      fetchAdminAgents(query).then(setItems).catch(() => setItems([]));
    }, [query])
  );

  return (
    <Screen>
      <View style={styles.searchRow}>
        <TextInput onChangeText={setKeyword} placeholder="상담사 검색" style={styles.input} value={keyword} />
        <TouchableOpacity onPress={() => setQuery(keyword)} style={styles.button}>
          <Text style={styles.buttonText}>검색</Text>
        </TouchableOpacity>
      </View>
      {items.map((item) => (
        <View key={item.agentId} style={styles.card}>
          <Text style={styles.name}>{item.name}</Text>
          <Text style={styles.meta}>{item.email}</Text>
          <Text style={styles.meta}>승인: {item.approvalStatus}</Text>
          <Text style={styles.meta}>직급: {item.grade}</Text>
        </View>
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  searchRow: { flexDirection: "row", gap: 8 },
  input: { flex: 1, borderWidth: 1, borderColor: "#d4d4d8", borderRadius: 8, minHeight: 42, paddingHorizontal: 12 },
  button: { backgroundColor: "#09090b", borderRadius: 8, minHeight: 42, justifyContent: "center", paddingHorizontal: 14 },
  buttonText: { color: "#fff", fontWeight: "800" },
  card: { backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 10, padding: 12, gap: 4 },
  name: { fontSize: 16, fontWeight: "900", color: "#09090b" },
  meta: { color: "#3f3f46", fontSize: 13 }
});
