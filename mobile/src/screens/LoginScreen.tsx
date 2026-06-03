import { useState } from "react";
import { Link, useRouter } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { login } from "@/api/auth";
import { ApiError } from "@/api/client";
import { Screen } from "@/components/Screen";
import { msg, resolveLoginError } from "@/messages";
import { setAccessToken } from "@/storage/authStorage";

export function LoginScreen() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function submit() {
    setPending(true);
    setErrorMessage(null);
    try {
      const response = await login({ email: email.trim(), password });
      await setAccessToken(response.accessToken);
      if (response.role === "ADMIN") {
        router.replace("/admin/menu");
      } else if (response.role === "AGENT") {
        router.replace("/agent-home");
      } else {
        router.replace("/(tabs)");
      }
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(resolveLoginError(error.code, error.message));
      } else {
        setErrorMessage(msg.ui("auth.loginFailed"));
      }
    } finally {
      setPending(false);
    }
  }

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>{msg.ui("auth.loginTitle")}</Text>
        <TextInput autoCapitalize="none" onChangeText={setEmail} placeholder={msg.ui("common.email")} style={styles.input} value={email} />
        <TextInput onChangeText={setPassword} placeholder={msg.ui("common.password")} secureTextEntry style={styles.input} value={password} />
        {errorMessage ? <Text style={styles.error}>{errorMessage}</Text> : null}
        <TouchableOpacity disabled={pending} onPress={submit} style={styles.button}>
          <Text style={styles.buttonText}>{pending ? msg.ui("auth.loginPending") : msg.ui("common.login")}</Text>
        </TouchableOpacity>
        <Link href="/auth/signup-customer" style={styles.link}>
          {msg.ui("auth.signupCustomer")}
        </Link>
        <Link href="/auth/signup-agent" style={styles.link}>
          {msg.ui("auth.signupAgent")}
        </Link>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  card: { gap: 12, backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 12, padding: 16 },
  title: { fontSize: 22, fontWeight: "900", color: "#09090b" },
  input: { borderWidth: 1, borderColor: "#d4d4d8", borderRadius: 8, minHeight: 44, paddingHorizontal: 12 },
  error: { color: "#dc2626", fontSize: 13 },
  button: { backgroundColor: "#09090b", borderRadius: 8, minHeight: 46, alignItems: "center", justifyContent: "center" },
  buttonText: { color: "#fff", fontWeight: "800" },
  link: { color: "#0f766e", fontSize: 14 }
});
