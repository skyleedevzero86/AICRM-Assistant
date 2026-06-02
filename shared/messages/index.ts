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

export function resolveApiError(error: unknown, fallbackUiKey: string): string {
  if (error instanceof Error && "code" in error) {
    const apiError = error as Error & { code?: string; message: string };
    if (apiError.message) {
      return apiError.message;
    }
    if (apiError.code) {
      const mapped = msg.error(apiError.code);
      if (mapped !== `errors.${apiError.code}`) {
        return mapped;
      }
      const clientMapped = msg.client(apiError.code);
      if (clientMapped !== `client.${apiError.code}`) {
        return clientMapped;
      }
    }
  }
  return msg.ui(fallbackUiKey);
}
