import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { Screen } from "@/components/Screen";

const quickMenus = ["문의하기", "채팅상담", "예약상담", "FAQ"];
const faqs = ["배송은 언제 오나요?", "환불은 어떻게 하나요?", "회원정보를 변경하고 싶어요."];

export function HomeScreen() {
  return (
    <Screen>
      <View style={styles.hero}>
        <Text style={styles.heroEyebrow}>안녕하세요, 고객님</Text>
        <Text style={styles.heroTitle}>무엇을 도와드릴까요?</Text>
        <TouchableOpacity style={styles.heroButton}>
          <Text style={styles.heroButtonText}>빠른 문의하기</Text>
        </TouchableOpacity>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>진행 중 문의</Text>
        <View style={styles.emptyCard}>
          <Text style={styles.emptyText}>접수한 문의가 있으면 이곳에서 상태를 확인할 수 있습니다.</Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>빠른 메뉴</Text>
        <View style={styles.quickGrid}>
          {quickMenus.map((label) => (
            <TouchableOpacity key={label} style={styles.quickItem}>
              <Text style={styles.quickText}>{label}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>자주 묻는 질문</Text>
        <View style={styles.listCard}>
          {faqs.map((faq) => (
            <Text key={faq} style={styles.faqItem}>
              {faq}
            </Text>
          ))}
        </View>
      </View>
    </Screen>
  );
}

const styles = StyleSheet.create({
  hero: {
    backgroundColor: "#09090b",
    borderRadius: 12,
    padding: 20
  },
  heroEyebrow: {
    color: "#d4d4d8",
    fontSize: 14
  },
  heroTitle: {
    color: "#ffffff",
    fontSize: 26,
    fontWeight: "900",
    marginTop: 8
  },
  heroButton: {
    alignItems: "center",
    alignSelf: "flex-start",
    backgroundColor: "#ffffff",
    borderRadius: 8,
    marginTop: 20,
    minHeight: 44,
    paddingHorizontal: 16,
    justifyContent: "center"
  },
  heroButtonText: {
    color: "#09090b",
    fontSize: 14,
    fontWeight: "800"
  },
  section: {
    gap: 10
  },
  sectionTitle: {
    color: "#09090b",
    fontSize: 17,
    fontWeight: "900"
  },
  emptyCard: {
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 10,
    borderStyle: "dashed",
    borderWidth: 1,
    padding: 18
  },
  emptyText: {
    color: "#52525b",
    fontSize: 14,
    lineHeight: 22
  },
  quickGrid: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8
  },
  quickItem: {
    alignItems: "center",
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 10,
    borderWidth: 1,
    flexBasis: "48%",
    minHeight: 72,
    justifyContent: "center"
  },
  quickText: {
    color: "#18181b",
    fontSize: 14,
    fontWeight: "800"
  },
  listCard: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 10,
    borderWidth: 1
  },
  faqItem: {
    borderBottomColor: "#f4f4f5",
    borderBottomWidth: 1,
    color: "#27272a",
    fontSize: 14,
    fontWeight: "700",
    padding: 16
  }
});
