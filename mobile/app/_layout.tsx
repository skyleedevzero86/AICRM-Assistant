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
        <Stack.Screen name="agent-home" options={{ title: "상담원 홈" }} />
        <Stack.Screen name="account" options={{ title: "내 정보" }} />
        <Stack.Screen name="admin/menu" options={{ title: "관리자 메뉴" }} />
        <Stack.Screen name="admin/agents" options={{ title: "상담사 회원 목록" }} />
        <Stack.Screen name="admin/customers" options={{ title: "고객 회원 목록" }} />
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen name="tickets/[ticketId]" options={{ title: "문의 상세" }} />
      </Stack>
    </>
  );
}
