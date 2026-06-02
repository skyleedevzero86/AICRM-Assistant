import { useState } from "react";
import { Link, useRouter } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { login, signupCustomer } from "@/api/auth";
import { ApiError } from "@/api/client";
import { Screen } from "@/components/Screen";
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
      const normalizedEmail = email.trim();
      await signupCustomer({ name: name.trim(), email: normalizedEmail, password });
      const loginResponse = await login({ email: normalizedEmail, password });
      await setAccessToken(loginResponse.accessToken);
      router.replace("/(tabs)");
    } catch (error) {
      setErrorMessage(error instanceof ApiError ? error.message : "회원가입에 실패했습니다.");
    } finally {
      setPending(false);
    }
  }

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>고객 회원가입</Text>
        <TextInput onChangeText={setName} placeholder="이름" style={styles.input} value={name} />
        <TextInput autoCapitalize="none" onChangeText={setEmail} placeholder="이메일" style={styles.input} value={email} />
        <TextInput onChangeText={setPassword} placeholder="비밀번호(8자 이상)" secureTextEntry style={styles.input} value={password} />
        {errorMessage ? <Text style={styles.error}>{errorMessage}</Text> : null}
        <TouchableOpacity disabled={pending} onPress={submit} style={styles.button}>
          <Text style={styles.buttonText}>{pending ? "가입 중..." : "고객 회원가입"}</Text>
        </TouchableOpacity>
        <Link href="/auth/login" style={styles.link}>로그인으로 이동</Link>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  card: { gap: 12, backgroundColor: "#fff", borderWidth: 1, borderColor: "#e4e4e7", borderRadius: 12, padding: 16 },
  title: { fontSize: 22, fontWeight: "900", color: "#09090b" },
  input: { borderWidth: 1, borderColor: "#d4d4d8", borderRadius: 8, minHeight: 44, paddingHorizontal: 12 },
  error: { color: "#dc2626", fontSize: 13 },
  button: { backgroundColor: "#0f766e", borderRadius: 8, minHeight: 46, alignItems: "center", justifyContent: "center" },
  buttonText: { color: "#fff", fontWeight: "800" },
  link: { color: "#0f766e", fontSize: 14 }
});
