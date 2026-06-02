import { useState } from "react";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { useRouter } from "expo-router";
import { createCustomerInquiry } from "@/api/customer";
import { ApiError } from "@/api/client";
import type { CreateCustomerInquiryResponse } from "@/api/types";
import { CategoryPicker } from "@/components/CategoryPicker";
import { ErrorMessage } from "@/components/ErrorMessage";
import { Screen } from "@/components/Screen";
import { TicketStatusBadge } from "@/components/TicketStatusBadge";
import { loadCustomerTickets } from "@/utils/ticket-sync";

export function InquiryScreen() {
  const router = useRouter();
  const [customerName, setCustomerName] = useState("");
  const [phone, setPhone] = useState("");
  const [email, setEmail] = useState("");
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [pending, setPending] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [categoryError, setCategoryError] = useState<string | null>(null);
  const [success, setSuccess] = useState<CreateCustomerInquiryResponse | null>(null);

  function resetForm() {
    setCategoryId(null);
    setTitle("");
    setContent("");
    setSubmitError(null);
    setCategoryError(null);
    setSuccess(null);
  }

  function resetAll() {
    setCustomerName("");
    setPhone("");
    setEmail("");
    resetForm();
  }

  async function submit() {
    setSubmitError(null);
    setCategoryError(null);

    if (!customerName.trim() || !phone.trim() || !email.trim() || !title.trim() || !content.trim()) {
      setSubmitError("필수 항목을 모두 입력해 주세요.");
      return;
    }

    if (!categoryId) {
      setCategoryError("소분류까지 선택해 주세요.");
      return;
    }

    setPending(true);
    try {
      const result = await createCustomerInquiry({
        customerName: customerName.trim(),
        phone: phone.trim(),
        email: email.trim(),
        title: title.trim(),
        content: content.trim(),
        categoryId
      });

      await loadCustomerTickets();

      setSuccess(result);
      setTitle("");
      setContent("");
      setCategoryId(null);
    } catch (error) {
      setSubmitError(error instanceof ApiError ? error.message : "문의 접수에 실패했습니다.");
    } finally {
      setPending(false);
    }
  }

  if (success) {
    return (
      <Screen>
        <View style={styles.successCard}>
          <Text style={styles.successTitle}>문의가 접수되었습니다.</Text>
          <Text style={styles.successTicketNo}>티켓 번호: {success.ticketNo}</Text>
          <View style={styles.statusRow}>
            <Text style={styles.statusLabel}>상태</Text>
            <TicketStatusBadge status={success.status} />
          </View>
          <View style={styles.successActions}>
            <TouchableOpacity onPress={() => router.push("/(tabs)/tickets")} style={styles.primaryButton}>
              <Text style={styles.primaryButtonText}>내 문의 보기</Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={resetForm} style={styles.secondaryButton}>
              <Text style={styles.secondaryButtonText}>새 문의 작성</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Screen>
    );
  }

  return (
    <Screen>
      <View>
        <Text style={styles.title}>문의하기</Text>
        <Text style={styles.description}>문의 내용을 남기면 상담원이 확인 후 답변드립니다.</Text>
      </View>
      {submitError ? <ErrorMessage message={submitError} /> : null}
      <View style={styles.form}>
        <Input label="이름" value={customerName} onChangeText={setCustomerName} />
        <Input label="연락처" value={phone} onChangeText={setPhone} keyboardType="phone-pad" />
        <Input label="이메일" value={email} onChangeText={setEmail} keyboardType="email-address" />
        <CategoryPicker error={categoryError ?? undefined} onChange={setCategoryId} value={categoryId} />
        <Input label="제목" value={title} onChangeText={setTitle} />
        <Input label="문의 내용" value={content} onChangeText={setContent} multiline />
        <TouchableOpacity disabled={pending} onPress={submit} style={[styles.button, pending && styles.disabled]}>
          <Text style={styles.buttonText}>{pending ? "접수 중..." : "접수하기"}</Text>
        </TouchableOpacity>
        <TouchableOpacity disabled={pending} onPress={resetAll} style={styles.secondaryButton}>
          <Text style={styles.secondaryButtonText}>입력 초기화</Text>
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
  },
  secondaryButton: {
    alignItems: "center",
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 8,
    borderWidth: 1,
    minHeight: 44,
    justifyContent: "center"
  },
  secondaryButtonText: {
    color: "#27272a",
    fontSize: 14,
    fontWeight: "800"
  },
  successCard: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 12,
    borderWidth: 1,
    gap: 14,
    padding: 20
  },
  successTitle: {
    color: "#09090b",
    fontSize: 20,
    fontWeight: "900"
  },
  successTicketNo: {
    color: "#27272a",
    fontSize: 15,
    fontWeight: "700"
  },
  statusRow: {
    alignItems: "center",
    flexDirection: "row",
    gap: 10
  },
  statusLabel: {
    color: "#52525b",
    fontSize: 14,
    fontWeight: "700"
  },
  successActions: {
    gap: 10,
    marginTop: 4
  },
  primaryButton: {
    alignItems: "center",
    backgroundColor: "#0f766e",
    borderRadius: 8,
    minHeight: 48,
    justifyContent: "center"
  },
  primaryButtonText: {
    color: "#ffffff",
    fontSize: 15,
    fontWeight: "900"
  }
});
