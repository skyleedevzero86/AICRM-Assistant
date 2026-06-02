import { useState } from "react";
import { Link, useRouter } from "expo-router";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { login } from "@/api/auth";
import { ApiError } from "@/api/client";
import { Screen } from "@/components/Screen";
import { setAccessToken } from "@/storage/authStorage";

function getLoginErrorMessage(error: ApiError): string {
  if (error.code === "AUTH_FAILED") {
    return "이메일 또는 비밀번호가 올바르지 않습니다.";
  }
  if (error.code === "AGENT_APPROVAL_REQUIRED") {
    return "상담원 계정 승인이 필요합니다.";
  }
  if (error.code === "ACCOUNT_SUSPENDED") {
    return "정지된 계정입니다. 관리자에게 문의하세요.";
  }
  if (error.code === "ACCOUNT_WITHDRAWN") {
    return "탈퇴 처리된 계정입니다.";
  }
  return error.message;
}

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
      router.replace("/(tabs)");
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(getLoginErrorMessage(error));
      } else {
        setErrorMessage("로그인에 실패했습니다.");
      }
    } finally {
      setPending(false);
    }
  }

  return (
    <Screen>
      <View style={styles.card}>
        <Text style={styles.title}>로그인</Text>
        <TextInput autoCapitalize="none" onChangeText={setEmail} placeholder="이메일" style={styles.input} value={email} />
        <TextInput onChangeText={setPassword} placeholder="비밀번호" secureTextEntry style={styles.input} value={password} />
        {errorMessage ? <Text style={styles.error}>{errorMessage}</Text> : null}
        <TouchableOpacity disabled={pending} onPress={submit} style={styles.button}>
          <Text style={styles.buttonText}>{pending ? "로그인 중..." : "로그인"}</Text>
        </TouchableOpacity>
        <Link href="/auth/signup-customer" style={styles.link}>고객 회원가입</Link>
        <Link href="/auth/signup-agent" style={styles.link}>상담원 회원가입</Link>
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
