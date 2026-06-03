import type { Href } from "expo-router";
import { useRouter } from "expo-router";
import { useEffect, useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { msg } from "@/messages";
import { getAccessToken, getAccessTokenRole } from "@/storage/authStorage";

type Props = {
  children: React.ReactNode;
};

export function RequireAdmin({ children }: Props) {
  const router = useRouter();
  const [allowed, setAllowed] = useState(false);

  useEffect(() => {
    let active = true;

    async function verify() {
      const token = await getAccessToken();
      const role = await getAccessTokenRole();
      if (!active) {
        return;
      }
      if (token && role === "ADMIN") {
        setAllowed(true);
        return;
      }
      router.replace("/auth/login" as Href);
    }

    void verify();

    return () => {
      active = false;
    };
  }, [router]);

  if (!allowed) {
    return (
      <View style={styles.placeholder}>
        <Text style={styles.text}>{msg.ui("admin.loginRequiredShort")}</Text>
      </View>
    );
  }

  return children;
}

const styles = StyleSheet.create({
  placeholder: { flex: 1, alignItems: "center", justifyContent: "center", padding: 24 },
  text: { color: "#52525b", fontSize: 14 }
});
