import { useState } from "react";
import { Alert, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { createCustomerInquiry } from "@/api/customer";
import { Screen } from "@/components/Screen";

const DEFAULT_CATEGORY_ID = 1;

export function InquiryScreen() {
  const [customerName, setCustomerName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [pending, setPending] = useState(false);

  async function submit() {
    if (!customerName || !phone || !email || !title || !content) {
      Alert.alert("입력 확인", "필수 항목을 모두 입력해 주세요.");
      return;
    }

    setPending(true);
    try {
      const result = await createCustomerInquiry({
        customerName,
        phone,
        email,
        title,
        content,
        categoryId: DEFAULT_CATEGORY_ID
      });
      Alert.alert("접수 완료", `티켓 번호: ${result.ticketNo}`);
      setTitle("");
      setContent("");
    } catch (error) {
      Alert.alert("접수 실패", error instanceof Error ? error.message : "문의 접수에 실패했습니다.");
    } finally {
      setPending(false);
    }
  }

  return (
    <Screen>
      <View>
        <Text style={styles.title}>문의하기</Text>
        <Text style={styles.description}>문의 내용을 남기면 상담원이 확인 후 답변드립니다.</Text>
      </View>
      <View style={styles.form}>
        <Input label="이름" value={customerName} onChangeText={setCustomerName} />
        <Input label="연락처" value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
        <Input label="이메일" value={email} onChangeText={setEmail} keyboardType="email-address" />
        <Input label="제목" value={title} onChangeText={setTitle} />
        <Input label="문의 내용" value={content} onChangeText={setContent} multiline />
        <TouchableOpacity disabled={pending} onPress={submit} style={[styles.button, pending && styles.disabled]}>
          <Text style={styles.buttonText}>{pending ? "접수 중..." : "접수하기"}</Text>
        </TouchableOpacity>
      </View>
    </Screen>
  );
}

type InputProps = {
  label: string;
  value: string;
  onChangeText: (value: string) => void;
  keyboardType?: "default" | "email-address" | "phone-pad";
  multiline?: boolean;
};

function Input({ label, value, onChangeText, keyboardType = "default", multiline }: InputProps) {
  return (
    <View style={styles.inputGroup}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        keyboardType={keyboardType}
        multiline={multiline}
        onChangeText={onChangeText}
        style={[styles.input, multiline && styles.textarea]}
        value={value}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  title: {
    color: "#09090b",
    fontSize: 22,
    fontWeight: "900"
  },
  description: {
    color: "#71717a",
    fontSize: 14,
    lineHeight: 22,
    marginTop: 6
  },
  form: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 12,
    borderWidth: 1,
    gap: 14,
    padding: 16
  },
  inputGroup: {
    gap: 6
  },
  label: {
    color: "#3f3f46",
    fontSize: 14,
    fontWeight: "800"
  },
  input: {
    borderColor: "#d4d4d8",
    borderRadius: 8,
    borderWidth: 1,
    color: "#09090b",
    fontSize: 15,
    minHeight: 46,
    paddingHorizontal: 12
  },
  textarea: {
    minHeight: 130,
    paddingTop: 12,
    textAlignVertical: "top"
  },
  button: {
    alignItems: "center",
    backgroundColor: "#0f766e",
    borderRadius: 8,
    minHeight: 48,
    justifyContent: "center"
  },
  disabled: {
    opacity: 0.55
  },
  buttonText: {
    color: "#ffffff",
    fontSize: 15,
    fontWeight: "900"
  }
});
