import { useState } from "react";
import { Link } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { signupAgent } from "@/api/auth";
import { Screen } from "@/components/Screen";
import { msg, resolveApiError } from "@/messages";

export function AgentSignupScreen() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [employeeNo, setEmployeeNo] = useState("");
  const [password, setPassword] = useState("");
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  async function submit() {
    setPending(true);
    setErrorMessage(null);
    try {
      await signupAgent({
        name: name.trim(),
        email: email.trim(),
        password,
        employeeNo: employeeNo.trim()
      });
      setDone(true);
    } catch (error) {
      setErrorMessage(resolveApiError(error, "auth.signupFailed"));
    } finally {
      setPending(false);
    }
  }

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>{msg.ui("auth.agentSignupTitle")}</Text>
        <Text style={styles.description}>{msg.ui("auth.agentSignupDescription")}</Text>
        <TextInput onChangeText={setName} placeholder={msg.ui("common.name")} style={styles.input} value={name} />
        <TextInput
          keyboardType="number-pad"
          maxLength={16}
          onChangeText={(value) => setEmployeeNo(value.replace(/\D/g, "").slice(0, 16))}
          placeholder={msg.ui("auth.employeeNo")}
          style={styles.input}
          value={employeeNo}
        />
        <Text style={styles.hint}>{msg.ui("auth.employeeNoHintMobile")}</Text>
        <TextInput autoCapitalize="none" onChangeText={setEmail} placeholder={msg.ui("common.email")} style={styles.input} value={email} />
        <TextInput onChangeText={setPassword} placeholder={msg.ui("common.passwordMin")} secureTextEntry style={styles.input} value={password} />
        {done ? <Text style={styles.success}>{msg.ui("auth.signupAgentSuccess")}</Text> : null}
        {errorMessage ? <Text style={styles.error}>{errorMessage}</Text> : null}
        <TouchableOpacity disabled={pending} onPress={submit} style={styles.button}>
          <Text style={styles.buttonText}>{pending ? msg.ui("auth.signupSubmitting") : msg.ui("auth.agentSignupSubmit")}</Text>
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
  description: { color: "#52525b", fontSize: 13 },
  hint: { color: "#71717a", fontSize: 12 },
  input: { borderWidth: 1, borderColor: "#d4d4d8", borderRadius: 8, minHeight: 44, paddingHorizontal: 12 },
  success: { color: "#0f766e", fontSize: 13 },
  error: { color: "#dc2626", fontSize: 13 },
  button: { backgroundColor: "#09090b", borderRadius: 8, minHeight: 46, alignItems: "center", justifyContent: "center" },
  buttonText: { color: "#fff", fontWeight: "800" },
  link: { color: "#0f766e", fontSize: 14 }
});
