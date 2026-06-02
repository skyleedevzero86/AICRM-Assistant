import { useEffect } from "react";
import { useRouter } from "expo-router";
import { getAccessToken } from "@/storage/authStorage";

export default function IndexRoute() {
  const router = useRouter();

  useEffect(() => {
    getAccessToken().then((token) => {
      if (token) {
        router.replace("/(tabs)");
      } else {
        router.replace("/auth/login");
      }
    });
  }, [router]);

  return null;
}
