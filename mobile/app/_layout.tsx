import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";

export default function RootLayout() {
  return (
    <>
      <StatusBar style="dark" />
      <Stack>
        <Stack.Screen name="index" options={{ headerShown: false }} />
        <Stack.Screen name="auth/login" options={{ title: "로그인" }} />
        <Stack.Screen name="auth/signup-customer" options={{ title: "고객 회원가입" }} />
        <Stack.Screen name="auth/signup-agent" options={{ title: "상담원 회원가입" }} />
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="tickets/[ticketId]" options={{ title: "문의 상세" }} />
      </Stack>
    </>
  );
}
