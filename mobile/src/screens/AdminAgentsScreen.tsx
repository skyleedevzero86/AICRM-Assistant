import { useCallback, useEffect, useState } from "react";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { useFocusEffect } from "expo-router";
import { fetchAdminAgents, updateAdminAgent } from "@/api/admin";
import { ApiError, AUTH_REQUIRED_CODE } from "@/api/client";
import type { AdminAgentUser } from "@/api/types";
import { Screen } from "@/components/Screen";
import { msg } from "@/messages";
import { getAccessToken } from "@/storage/authStorage";
import { adminUserFieldStyle } from "@/utils/admin-user-display";

function AgentCard({ item, onSaved }: { item: AdminAgentUser; onSaved: () => void }) {
  const [employeeNo, setEmployeeNo] = useState(item.employeeNo);
  const [name, setName] = useState(item.name);
  const [email, setEmail] = useState(item.email);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setEmployeeNo(item.employeeNo);
    setName(item.name);
    setEmail(item.email);
  }, [item]);

  const fieldStyle = adminUserFieldStyle(item.withdrawnYn, item.suspendedYn);

  async function save() {
    setSaving(true);
    setError(null);
    try {
      await updateAdminAgent(item.userId, { name, email, employeeNo });
      onSaved();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : msg.ui("admin.updateProfileFailed"));
    } finally {
      setSaving(false);
    }
  }

  return (
    <View style={styles.card}>
      <TextInput onChangeText={setEmployeeNo} style={[styles.input, fieldStyle]} value={employeeNo} />
      <TextInput onChangeText={setName} style={[styles.input, fieldStyle]} value={name} />
      <TextInput
        autoCapitalize="none"
        keyboardType="email-address"
        onChangeText={setEmail}
        style={[styles.input, fieldStyle]}
        value={email}
      />
      <Text style={styles.meta}>
        {msg.ui("admin.approvalStatus")}: {msg.label("approvalStatus", item.approvalStatus)}
      </Text>
      <Text style={styles.meta}>
        {msg.ui("admin.suspended")}: {msg.label("yn", item.suspendedYn)} / {msg.ui("admin.withdrawn")}:{" "}
        {msg.label("yn", item.withdrawnYn)}
      </Text>
      {error ? <Text style={styles.error}>{error}</Text> : null}
      <TouchableOpacity disabled={saving} onPress={() => void save()} style={styles.saveButton}>
        <Text style={styles.saveButtonText}>{saving ? msg.ui("common.saving") : msg.ui("common.save")}</Text>
      </TouchableOpacity>
    </View>
  );
}

export function AdminAgentsScreen() {
  const [keyword, setKeyword] = useState("");
  const [query, setQuery] = useState("");
  const [items, setItems] = useState<AdminAgentUser[]>([]);
  const [reloadKey, setReloadKey] = useState(0);

  const load = useCallback(async () => {
    const token = await getAccessToken();
    if (!token) {
      setItems([]);
      return;
    }
    try {
      const data = await fetchAdminAgents(query);
      setItems(data);
    } catch (error) {
      setItems([]);
      if (error instanceof ApiError && error.code !== AUTH_REQUIRED_CODE && error.code !== "UNAUTHORIZED") {
        console.warn(error.message);
      }
    }
  }, [query]);

  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load, reloadKey])
  );

  return (
    <Screen>
      <View style={styles.searchRow}>
        <TextInput
          onChangeText={setKeyword}
          placeholder={msg.ui("admin.searchAgentsMobile")}
          style={styles.searchInput}
          value={keyword}
        />
        <TouchableOpacity onPress={() => setQuery(keyword)} style={styles.searchButton}>
          <Text style={styles.searchButtonText}>{msg.ui("common.search")}</Text>
        </TouchableOpacity>
      </View>
      {items.map((item) => (
        <AgentCard item={item} key={item.agentId} onSaved={() => setReloadKey((value) => value + 1)} />
      ))}
    </Screen>
  );
}

const styles = StyleSheet.create({
  searchRow: { flexDirection: "row", gap: 8 },
  searchInput: { flex: 1, borderWidth: 1, borderColor: "#d4d4d8", borderRadius: 8, minHeight: 42, paddingHorizontal: 12 },
  searchButton: { backgroundColor: "#09090b", borderRadius: 8, minHeight: 42, justifyContent: "center", paddingHorizontal: 14 },
  searchButtonText: { color: "#fff", fontWeight: "800" },
  card: { backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 10, padding: 12, gap: 8 },
  input: { borderWidth: 1, borderColor: "#d4d4d8", borderRadius: 8, minHeight: 42, paddingHorizontal: 12 },
  meta: { color: "#52525b", fontSize: 13 },
  error: { color: "#dc2626", fontSize: 13, fontWeight: "700" },
  saveButton: { backgroundColor: "#0f766e", borderRadius: 8, minHeight: 40, alignItems: "center", justifyContent: "center" },
  saveButtonText: { color: "#fff", fontWeight: "800" }
});
