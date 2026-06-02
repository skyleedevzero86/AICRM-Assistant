import { useState } from "react";
import { Link, useRouter } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { login, signupCustomer } from "@/api/auth";
import { Screen } from "@/components/Screen";
import { msg, resolveApiError } from "@/messages";
import { setAccessToken } from "@/storage/authStorage";

export function CustomerSignupScreen() {
  const router = useRouter();
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function submit() {
    setPending(true);
    setErrorMessage(null);
    try {
      await signupCustomer({ name: name.trim(), email: email.trim(), password });
      const response = await login({ email: email.trim(), password });
      await setAccessToken(response.accessToken);
      router.replace("/(tabs)");
    } catch (error) {
      setErrorMessage(resolveApiError(error, "auth.signupFailed"));
    } finally {
      setPending(false);
    }
  }

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>{msg.ui("auth.customerSignupTitle")}</Text>
        <TextInput onChangeText={setName} placeholder={msg.ui("common.name")} style={styles.input} value={name} />
        <TextInput autoCapitalize="none" onChangeText={setEmail} placeholder={msg.ui("common.email")} style={styles.input} value={email} />
        <TextInput onChangeText={setPassword} placeholder={msg.ui("common.passwordMin")} secureTextEntry style={styles.input} value={password} />
        {errorMessage ? <Text style={styles.error}>{errorMessage}</Text> : null}
        <TouchableOpacity disabled={pending} onPress={submit} style={styles.button}>
          <Text style={styles.buttonText}>{pending ? msg.ui("auth.signupPending") : msg.ui("auth.signupCustomer")}</Text>
        </TouchableOpacity>
        <Link href="/auth/login" style={styles.link}>
          {msg.ui("auth.goToLogin")}
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
