import {
  normalizeYnFlag,
  resolveAdminUserDisplayState,
  type YnFlag
} from "../../shared/admin-user-display";

export { normalizeYnFlag, resolveAdminUserDisplayState };
export type { YnFlag };

export function adminUserRowClassName(withdrawnYn: string, suspendedYn: string): string {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  if (state === "withdrawn") {
    return "bg-red-50";
  }
  if (state === "suspended") {
    return "bg-purple-50";
  }
  return "";
}

export function adminUserFieldClassName(withdrawnYn: string, suspendedYn: string): string {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  if (state === "withdrawn") {
    return "text-red-600 line-through decoration-red-600";
  }
  if (state === "suspended") {
    return "text-purple-700";
  }
  return "text-zinc-900";
}

export function adminUserInputClassName(withdrawnYn: string, suspendedYn: string): string {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  const base =
    "w-full rounded border px-2 py-1 text-sm outline-none focus:border-teal-600 bg-white [color:var(--admin-field-color)] [-webkit-text-fill-color:var(--admin-field-color)]";
  if (state === "withdrawn") {
    return `${base} border-red-200 line-through decoration-red-600 [--admin-field-color:#dc2626]`;
  }
  if (state === "suspended") {
    return `${base} border-purple-300 [--admin-field-color:#7e22ce]`;
  }
  return `${base} border-zinc-300 [--admin-field-color:#18181b]`;
}
