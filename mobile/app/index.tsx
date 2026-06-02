import { useEffect } from "react";
import { useRouter } from "expo-router";
import { getAccessToken, getAccessTokenRole } from "@/storage/authStorage";

export default function IndexRoute() {
  const router = useRouter();

  useEffect(() => {
    async function redirect() {
      const token = await getAccessToken();
      if (!token) {
        router.replace("/auth/login");
        return;
      }
      const role = await getAccessTokenRole();
      if (role === "ADMIN") {
        router.replace("/admin/menu");
        return;
      }
      if (role === "AGENT") {
        router.replace("/agent-home");
        return;
      }
      router.replace("/(tabs)");
    }

    void redirect();
  }, [router]);

  return null;
}
