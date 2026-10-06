import { Stack } from "expo-router";
import { RequireAdmin } from "@/components/RequireAdmin";

export default function AdminLayout() {
  return (
    <RequireAdmin>
      <Stack screenOptions={{ headerShown: true }} />
    </RequireAdmin>
  );
}
