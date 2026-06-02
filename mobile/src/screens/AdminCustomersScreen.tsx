import { useCallback, useState } from "react";
import { useFocusEffect } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { fetchAdminCustomers } from "@/api/admin";
import { ApiError, AUTH_REQUIRED_CODE } from "@/api/client";
import type { AdminCustomerUser } from "@/api/types";
import { getAccessToken } from "@/storage/authStorage";
import { Screen } from "@/components/Screen";

export function AdminCustomersScreen() {
  const [keyword, setKeyword] = useState("");
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<AdminCustomerUser[]>([]);

  useFocusEffect(
    useCallback(() => {
      let active = true;

      async function load() {
        const token = await getAccessToken();
        if (!token) {
          if (active) {
            setItems([]);
          }
          return;
        }
        try {
          const data = await fetchAdminCustomers(query);
          if (active) {
            setItems(data);
          }
        } catch (error) {
          if (active) {
            setItems([]);
          }
          if (error instanceof ApiError && error.code !== AUTH_REQUIRED_CODE && error.code !== "UNAUTHORIZED") {
            console.warn(error.message);
          }
        }
      }

      void load();

      return () => {
        active = false;
      };
    }, [query])
  );

  return (
    <Screen>
      <View style={styles.searchRow}>
        <TextInput onChangeText={setKeyword} placeholder="고객 검색" style={styles.input} value={keyword} />
        <TouchableOpacity onPress={() => setQuery(keyword)} style={styles.button}>
          <Text style={styles.buttonText}>검색</Text>
        </TouchableOpacity>
      </View>
      {items.map((item) => (
        <View key={item.userId} style={styles.card}>
          <Text style={styles.name}>{item.name}</Text>
          <Text style={styles.meta}>{item.email}</Text>
          <Text style={styles.meta}>연락처: {item.phone || "-"}</Text>
          <Text style={styles.meta}>정지: {item.suspendedYn} / 탈퇴: {item.withdrawnYn}</Text>
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
