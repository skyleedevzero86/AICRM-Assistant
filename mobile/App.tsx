import { StatusBar } from "expo-status-bar";
import { SafeAreaView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useState } from "react";
import { ChatScreen } from "./src/screens/ChatScreen";
import { HomeScreen } from "./src/screens/HomeScreen";
import { InquiryScreen } from "./src/screens/InquiryScreen";
import { ProfileScreen } from "./src/screens/ProfileScreen";
import { TicketsScreen } from "./src/screens/TicketsScreen";

type TabKey = "home" | "inquiry" | "chat" | "tickets" | "profile";

const tabs: Array<{ key: TabKey; label: string }> = [
  { key: "home", label: "홈" },
  { key: "inquiry", label: "문의" },
  { key: "chat", label: "상담" },
  { key: "tickets", label: "내 문의" },
  { key: "profile", label: "마이" }
];

export default function App() {
  const [activeTab, setActiveTab] = useState<TabKey>("home");

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar style="dark" />
      <View style={styles.header}>
        <Text style={styles.headerEyebrow}>AICRM</Text>
        <Text style={styles.headerTitle}>고객센터</Text>
      </View>
      <View style={styles.content}>{renderScreen(activeTab)}</View>
      <View style={styles.tabBar}>
        {tabs.map((tab) => {
          const active = activeTab === tab.key;
          return (
            <TouchableOpacity
              accessibilityRole="button"
              key={tab.key}
              onPress={() => setActiveTab(tab.key)}
              style={[styles.tabItem, active && styles.activeTabItem]}
            >
              <Text style={[styles.tabLabel, active && styles.activeTabLabel]}>{tab.label}</Text>
            </TouchableOpacity>
          );
        })}
      </View>
    </SafeAreaView>
  );
}

function renderScreen(activeTab: TabKey) {
  switch (activeTab) {
    case "home":
      return <HomeScreen />;
    case "inquiry":
      return <InquiryScreen />;
    case "chat":
      return <ChatScreen />;
    case "tickets":
      return <TicketsScreen />;
    case "profile":
      return <ProfileScreen />;
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#f7f8fa"
  },
  header: {
    borderBottomColor: "#e4e4e7",
    borderBottomWidth: 1,
    backgroundColor: "#ffffff",
    paddingHorizontal: 20,
    paddingVertical: 14
  },
  headerEyebrow: {
    color: "#71717a",
    fontSize: 12,
    fontWeight: "600"
  },
  headerTitle: {
    color: "#09090b",
    fontSize: 18,
    fontWeight: "800",
    marginTop: 2
  },
  content: {
    flex: 1
  },
  tabBar: {
    flexDirection: "row",
    gap: 6,
    borderTopColor: "#e4e4e7",
    borderTopWidth: 1,
    backgroundColor: "#ffffff",
    paddingHorizontal: 8,
    paddingBottom: 8,
    paddingTop: 8
  },
  tabItem: {
    alignItems: "center",
    borderRadius: 8,
    flex: 1,
    minHeight: 48,
    justifyContent: "center"
  },
  activeTabItem: {
    backgroundColor: "#ecfdf5"
  },
  tabLabel: {
    color: "#71717a",
    fontSize: 12,
    fontWeight: "700"
  },
  activeTabLabel: {
    color: "#0f766e"
  }
});
