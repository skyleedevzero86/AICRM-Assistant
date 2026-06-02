import { resolveAdminUserDisplayState } from "../../shared/admin-user-display";
import type { YnFlag } from "../../shared/admin-user-display";

export { resolveAdminUserDisplayState };
export type { YnFlag };

export function adminUserFieldClassName(withdrawnYn: YnFlag, suspendedYn: YnFlag): string {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  if (state === "withdrawn") {
    return "text-red-600 line-through";
  }
  if (state === "suspended") {
    return "text-purple-700";
  }
  return "text-zinc-900";
}

export function adminUserInputClassName(withdrawnYn: YnFlag, suspendedYn: YnFlag): string {
  const state = resolveAdminUserDisplayState(withdrawnYn, suspendedYn);
  const base = "w-full rounded border px-2 py-1 text-sm outline-none focus:border-teal-600";
  if (state === "withdrawn") {
    return `${base} border-red-200 text-red-600 line-through`;
  }
  if (state === "suspended") {
    return `${base} border-purple-200 text-purple-700`;
  }
  return `${base} border-zinc-300 text-zinc-900`;
}
