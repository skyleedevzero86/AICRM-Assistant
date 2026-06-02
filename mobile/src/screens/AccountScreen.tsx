import { useCallback, useState } from "react";
import { ActivityIndicator, Alert, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { useFocusEffect, useRouter } from "expo-router";
import { fetchMe, updateMe, withdrawMe } from "@/api/auth";
import { ApiError } from "@/api/client";
import { Screen } from "@/components/Screen";
import { msg } from "@/messages";
import { clearAccessToken } from "@/storage/authStorage";
import type { MeResponse } from "@/api/types";

export function AccountScreen() {
  const router = useRouter();
  const [me, setMe] = useState<MeResponse | null>(null);
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [phone, setPhone] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await fetchMe();
      setMe(data);
      setName(data.name);
      setPhone(data.phone ?? "");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : msg.ui("account.saveFailed"));
    } finally {
      setLoading(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      void load();
    }, [load])
  );

  async function save() {
    if (!me) {
      return;
    }
    setSaving(true);
    setNotice(null);
    setError(null);
    try {
      const updated = await updateMe({
        name,
        ...(password ? { password } : {}),
        ...(me.role === "CUSTOMER" ? { phone } : {})
      });
      setMe(updated);
      setPassword("");
      setNotice(msg.ui("account.saved"));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : msg.ui("account.saveFailed"));
    } finally {
      setSaving(false);
    }
  }

  async function logout() {
    await clearAccessToken();
    router.replace("/auth/login");
  }

  function confirmWithdraw() {
    Alert.alert(msg.ui("common.withdraw"), msg.ui("account.withdrawConfirm"), [
      { text: msg.ui("common.cancel"), style: "cancel" },
      {
        text: msg.ui("common.withdraw"),
        style: "destructive",
        onPress: () => {
          void (async () => {
            try {
              await withdrawMe();
              await clearAccessToken();
              router.replace("/auth/login");
            } catch (err) {
              setError(err instanceof ApiError ? err.message : msg.ui("account.saveFailed"));
            }
          })();
        }
      }
    ]);
  }

  const isCustomer = me?.role === "CUSTOMER";

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>{msg.ui("account.title")}</Text>
        <Text style={styles.description}>{msg.ui("account.description")}</Text>
        {loading ? <ActivityIndicator color="#0f766e" /> : null}
        {notice ? <Text style={styles.notice}>{notice}</Text> : null}
        {error ? <Text style={styles.error}>{error}</Text> : null}
        {me ? (
          <>
            <Field label={msg.ui("common.email")} value={me.email} editable={false} />
            <Field label={msg.ui("common.name")} value={name} onChangeText={setName} />
            {isCustomer ? (
              <Field label={msg.ui("common.phone")} value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
            ) : null}
            <Field
              label={msg.ui("common.passwordMin")}
              value={password}
              onChangeText={setPassword}
              placeholder={msg.ui("account.passwordOptional")}
              secureTextEntry
            />
            <TouchableOpacity disabled={saving} onPress={() => void save()} style={styles.primaryButton}>
              <Text style={styles.primaryButtonText}>
                {saving ? msg.ui("common.saving") : msg.ui("common.save")}
              </Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={() => void logout()} style={styles.secondaryButton}>
              <Text style={styles.secondaryButtonText}>{msg.ui("common.logout")}</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={confirmWithdraw} style={styles.dangerButton}>
              <Text style={styles.dangerButtonText}>{msg.ui("common.withdraw")}</Text>
            </TouchableOpacity>
          </>
        ) : null}
      </View>
    </Screen>
  );
}

function Field({
  label,
  value,
  onChangeText,
  editable = true,
  placeholder,
  secureTextEntry,
  keyboardType
}: {
  label: string;
  value: string;
  onChangeText?: (text: string) => void;
  editable?: boolean;
  placeholder?: string;
  secureTextEntry?: boolean;
  keyboardType?: "default" | "phone-pad";
}) {
  return (
    <View style={styles.field}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        editable={editable}
        keyboardType={keyboardType}
        onChangeText={onChangeText}
        placeholder={placeholder}
        secureTextEntry={secureTextEntry}
        style={[styles.input, !editable ? styles.inputDisabled : null]}
        value={value}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  card: { gap: 12, backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 12, padding: 16 },
  title: { fontSize: 22, fontWeight: "900", color: "#09090b" },
  description: { color: "#52525b", fontSize: 14, lineHeight: 20 },
  notice: { color: "#0f766e", fontSize: 14, fontWeight: "700" },
  error: { color: "#b91c1c", fontSize: 14, fontWeight: "700" },
  field: { gap: 6 },
  label: { color: "#52525b", fontSize: 13, fontWeight: "700" },
  input: {
    borderWidth: 1,
    borderColor: "#d4d4d8",
    borderRadius: 8,
    minHeight: 44,
    paddingHorizontal: 12,
    color: "#09090b"
  },
  inputDisabled: { backgroundColor: "#f4f4f5", color: "#71717a" },
  primaryButton: {
    backgroundColor: "#0f766e",
    borderRadius: 8,
    minHeight: 46,
    alignItems: "center",
    justifyContent: "center",
    marginTop: 4
  },
  primaryButtonText: { color: "#fff", fontWeight: "800" },
  secondaryButton: {
    borderWidth: 1,
    borderColor: "#d4d4d8",
    borderRadius: 8,
    minHeight: 46,
    alignItems: "center",
    justifyContent: "center"
  },
  secondaryButtonText: { color: "#18181b", fontWeight: "800" },
  dangerButton: {
    backgroundColor: "#fee2e2",
    borderRadius: 8,
    minHeight: 46,
    alignItems: "center",
    justifyContent: "center"
  },
  dangerButtonText: { color: "#b91c1c", fontWeight: "800" }
});
