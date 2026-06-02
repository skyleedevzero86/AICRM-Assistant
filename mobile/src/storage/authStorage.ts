import AsyncStorage from "@react-native-async-storage/async-storage";
import type { UserRole } from "@/api/types";

const ACCESS_TOKEN_KEY = "aicrm-access-token";

export async function getAccessToken(): Promise<string | null> {
  return AsyncStorage.getItem(ACCESS_TOKEN_KEY);
}

export async function setAccessToken(token: string): Promise<void> {
  await AsyncStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export async function clearAccessToken(): Promise<void> {
  await AsyncStorage.removeItem(ACCESS_TOKEN_KEY);
}

export async function getAccessTokenRole(): Promise<UserRole | null> {
  const token = await getAccessToken();
  if (!token) {
    return null;
  }
  const parts = token.split(".");
  if (parts.length < 2) {
    return null;
  }
  try {
    const payload = JSON.parse(atob(parts[1].replace(/-/g, "+").replace(/_/g, "/"))) as { role?: unknown };
    if (payload.role === "ADMIN" || payload.role === "AGENT" || payload.role === "CUSTOMER") {
      return payload.role;
    }
    return null;
  } catch {
    return null;
  }
}
