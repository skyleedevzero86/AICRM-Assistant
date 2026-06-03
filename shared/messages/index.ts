import catalog from "./ko.json";

type MessageParams = Record<string, string | number>;

function resolvePath(path: string): string | undefined {
  const parts = path.split(".");
  let node: unknown = catalog;
  for (const part of parts) {
    if (typeof node !== "object" || node === null || !(part in node)) {
      return undefined;
    }
    node = (node as Record<string, unknown>)[part];
  }
  return typeof node === "string" ? node : undefined;
}

export function formatMessage(template: string, params?: MessageParams): string {
  if (!params) {
    return template;
  }
  return template.replace(/\{(\w+)\}/g, (_, key: string) => String(params[key] ?? `{${key}}`));
}

export function getMessage(path: string, params?: MessageParams): string {
  const template = resolvePath(path);
  if (!template) {
    return path;
  }
  return formatMessage(template, params);
}

export const msg = {
  error(code: string, params?: MessageParams): string {
    return getMessage(`errors.${code}`, params);
  },
  client(code: string, params?: MessageParams): string {
    return getMessage(`client.${code}`, params);
  },
  validation(path: string): string {
    return getMessage(`validation.${path}`);
  },
  ui(path: string, params?: MessageParams): string {
    return getMessage(`ui.${path}`, params);
  },
  label(category: string, value: string): string {
    const resolved = resolvePath(`labels.${category}.${value}`);
    return resolved ?? value;
  }
};

export function resolveLoginError(code?: string, message?: string): string {
  if (code === "AUTH_FAILED") {
    return msg.error("AUTH_FAILED");
  }
  if (code === "AGENT_APPROVAL_REQUIRED") {
    return msg.ui("auth.loginAgentPending");
  }
  if (code === "ACCOUNT_SUSPENDED") {
    return msg.ui("auth.loginSuspended");
  }
  if (code === "ACCOUNT_WITHDRAWN") {
    return msg.ui("auth.loginWithdrawn");
  }
  if (code === "ACCOUNT_UNAVAILABLE") {
    return msg.ui("auth.loginAccountUnavailable");
  }
  if (message) {
    return message;
  }
  if (code) {
    const mapped = msg.error(code);
    if (mapped !== `errors.${code}`) {
      return mapped;
    }
  }
  return msg.ui("auth.loginFailed");
}

export function resolvePublicAuthError(code?: string, message?: string): string {
  return resolveLoginError(code, message);
}

function resolveApiErrorPayload(
  code: string | undefined,
  message: string | undefined,
  fallbackUiKey: string
): string {
  if (code === "AUTH_FAILED") {
    return msg.error("AUTH_FAILED");
  }
  if (message) {
    return message;
  }
  if (code) {
    const mapped = msg.error(code);
    if (mapped !== `errors.${code}`) {
      return mapped;
    }
    const clientMapped = msg.client(code);
    if (clientMapped !== `client.${code}`) {
      return clientMapped;
    }
  }
  return msg.ui(fallbackUiKey);
}

function readErrorCode(error: unknown): string | undefined {
  if (typeof error !== "object" || error === null || !("code" in error)) {
    return undefined;
  }
  const value = (error as { code?: unknown }).code;
  return typeof value === "string" && value.length > 0 ? value : undefined;
}

export function resolveApiError(error: unknown, fallbackUiKey: string): string {
  if (error instanceof Error) {
    return resolveApiErrorPayload(readErrorCode(error), error.message, fallbackUiKey);
  }
  return msg.ui(fallbackUiKey);
}
