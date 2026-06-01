import Constants from "expo-constants";
import { Platform } from "react-native";

const DEFAULT_PORT = 8080;

function normalizeBaseUrl(url: string): string {
  return url.replace(/\/$/, "");
}

function isTunnelHost(host: string): boolean {
  const lower = host.toLowerCase();
  return (
    lower.includes("exp.direct") ||
    lower.includes("ngrok") ||
    lower.includes("tunnel") ||
    lower.includes(".exp.host")
  );
}

function hostFromExpo(): string | null {
  const candidates = [
    Constants.expoConfig?.hostUri,
    (Constants.expoGoConfig as { debuggerHost?: string } | undefined)?.debuggerHost,
    (Constants.manifest2 as { extra?: { expoClient?: { hostUri?: string } } } | null)?.extra?.expoClient
      ?.hostUri
  ];

  for (const value of candidates) {
    if (!value) continue;
    const host = value.split(":")[0]?.trim();
    if (!host || host === "localhost" || host === "127.0.0.1") continue;
    if (isTunnelHost(host)) continue;
    return host;
  }

  return null;
}

function readPublicEnvApiBaseUrl(): string | undefined {
  const env = (globalThis as { process?: { env?: Record<string, string | undefined> } }).process?.env;
  const value = env?.EXPO_PUBLIC_API_BASE_URL?.trim();
  return value || undefined;
}

function readConfiguredApiBaseUrl(): string | undefined {
  const fromEnv = readPublicEnvApiBaseUrl();
  if (fromEnv) return normalizeBaseUrl(fromEnv);

  const fromExtra = (Constants.expoConfig?.extra?.apiBaseUrl as string | undefined)?.trim();
  if (fromExtra) return normalizeBaseUrl(fromExtra);

  return undefined;
}

function androidEmulatorBaseUrl(): string {
  return `http://10.0.2.2:${DEFAULT_PORT}`;
}

function lanBaseUrl(host: string): string {
  return `http://${host}:${DEFAULT_PORT}`;
}

export function getCandidateApiBaseUrls(): string[] {
  const ordered: string[] = [];
  const configured = readConfiguredApiBaseUrl();
  if (configured) ordered.push(configured);

  const expoHost = hostFromExpo();
  if (expoHost) ordered.push(lanBaseUrl(expoHost));

  if (Platform.OS === "android") {
    ordered.push(androidEmulatorBaseUrl());
  }

  if (Platform.OS === "ios") {
    ordered.push(`http://localhost:${DEFAULT_PORT}`);
  }

  ordered.push(`http://127.0.0.1:${DEFAULT_PORT}`);

  return [...new Set(ordered)];
}

export function resolveApiBaseUrl(): string {
  const candidates = getCandidateApiBaseUrls();
  return candidates[0] ?? `http://localhost:${DEFAULT_PORT}`;
}
