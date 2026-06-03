import { Tabs } from "expo-router";
import { Ionicons } from "@expo/vector-icons";

export default function TabLayout() {
  return (
    <Tabs
      screenOptions={{
        headerStyle: { backgroundColor: "#ffffff" },
        headerTitleStyle: { fontWeight: "800", color: "#09090b" },
        tabBarActiveTintColor: "#0f766e",
        tabBarInactiveTintColor: "#71717a",
        tabBarStyle: {
          backgroundColor: "#ffffff",
          borderTopColor: "#e4e4e7"
        }
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: "홈",
          headerTitle: "고객센터",
          tabBarIcon: ({ color, size }) => <Ionicons color={color} name="home-outline" size={size} />
        }}
      />
      <Tabs.Screen
        name="inquiry"
        options={{
          title: "문의",
          tabBarIcon: ({ color, size }) => <Ionicons color={color} name="create-outline" size={size} />
        }}
      />
      <Tabs.Screen
        name="tickets"
        options={{
          title: "내 문의",
          tabBarIcon: ({ color, size }) => <Ionicons color={color} name="list-outline" size={size} />
        }}
      />
      <Tabs.Screen
        name="account"
        options={{
          title: "내 정보",
          tabBarIcon: ({ color, size }) => <Ionicons color={color} name="person-outline" size={size} />
        }}
      />
    </Tabs>
  );
}
