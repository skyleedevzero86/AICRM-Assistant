import { useEffect, useState } from "react";
import { StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import {
  fetchCustomerTicket,
  fetchCustomerTicketMessages,
  updateCustomerInquiry
} from "@/api/customer";
import { ApiError } from "@/api/client";
import type { CustomerTicketDetail, Message } from "@/api/types";
import { CategoryPicker } from "@/components/CategoryPicker";
import { EmptyState } from "@/components/EmptyState";
import { ErrorMessage } from "@/components/ErrorMessage";
import { LoadingView } from "@/components/LoadingView";
import { Screen } from "@/components/Screen";
import { TicketStatusBadge } from "@/components/TicketStatusBadge";
import { TicketAttachmentSection } from "@/components/TicketAttachmentSection";
import { loadCustomerTickets } from "@/utils/ticket-sync";
import { formatDateTime, formatSenderType } from "@/utils/format";

export function TicketDetailScreen({ ticketId }: { ticketId: number }) {
  const [ticket, setTicket] = useState<CustomerTicketDetail | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);
  const [customerName, setCustomerName] = useState("");
  const [email, setEmail] = useState("");
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [categoryError, setCategoryError] = useState<string | null>(null);

  async function loadDetail() {
    setLoading(true);
    setErrorMessage(null);
    try {
      const [detail, history] = await Promise.all([
        fetchCustomerTicket(ticketId),
        fetchCustomerTicketMessages(ticketId)
      ]);
      setTicket(detail);
      setMessages(history.filter((message) => message.messageType === "TEXT"));
      setCustomerName(detail.customerName);
      setEmail(detail.customerEmail);
      setCategoryId(detail.categoryId);
      setTitle(detail.subject);
      setContent(detail.inquiryContent);
    } catch (error) {
      setTicket(null);
      setMessages([]);
      setErrorMessage(error instanceof ApiError ? error.message : "문의 상세를 불러오지 못했습니다.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadDetail();
  }, [ticketId]);

  async function saveChanges() {
    if (!ticket) return;
    setSaveError(null);
    setCategoryError(null);

    if (!customerName.trim() || !email.trim() || !title.trim() || !content.trim()) {
      setSaveError("필수 항목을 모두 입력해 주세요.");
      return;
    }

    if (!categoryId) {
      setCategoryError("소분류까지 선택해 주세요.");
      return;
    }

    setSaving(true);
    try {
      const updated = await updateCustomerInquiry(ticket.ticketId, {
        customerName: customerName.trim(),
        email: email.trim(),
        categoryId,
        title: title.trim(),
        content: content.trim()
      });
      setTicket(updated);
      setEditing(false);
      await loadCustomerTickets();
      await loadDetail();
    } catch (error) {
      setSaveError(error instanceof ApiError ? error.message : "문의 수정에 실패했습니다.");
    } finally {
      setSaving(false);
    }
  }

  function cancelEdit() {
    if (!ticket) return;
    setCustomerName(ticket.customerName);
    setEmail(ticket.customerEmail);
    setCategoryId(ticket.categoryId);
    setTitle(ticket.subject);
    setContent(ticket.inquiryContent);
    setSaveError(null);
    setCategoryError(null);
    setEditing(false);
  }

  if (loading) {
    return (
      <Screen>
        <LoadingView label="문의 상세를 불러오는 중..." />
      </Screen>
    );
  }

  if (errorMessage) {
    return (
      <Screen>
        <ErrorMessage message={errorMessage} />
        <TouchableOpacity onPress={() => void loadDetail()} style={styles.retryButton}>
          <Text style={styles.retryText}>다시 시도</Text>
        </TouchableOpacity>
      </Screen>
    );
  }

  if (!ticket) {
    return (
      <Screen>
        <EmptyState description="티켓 정보를 찾을 수 없습니다." title="문의를 찾을 수 없습니다" />
      </Screen>
    );
  }

  const canEdit = ticket.status === "WAITING";
  const canUploadAttachment = ticket.status !== "CLOSED" && ticket.status !== "RESOLVED";

  return (
    <Screen>
      <View style={styles.headerCard}>
        <Text style={styles.ticketNo}>{ticket.ticketNo}</Text>
        {editing ? (
          <View style={styles.editForm}>
            {saveError ? <ErrorMessage message={saveError} /> : null}
            <Field label="이름" onChangeText={setCustomerName} value={customerName} />
            <Field label="이메일" keyboardType="email-address" onChangeText={setEmail} value={email} />
            <CategoryPicker error={categoryError ?? undefined} onChange={setCategoryId} value={categoryId} />
            <Field label="제목" onChangeText={setTitle} value={title} />
            <Field label="문의 내용" multiline onChangeText={setContent} value={content} />
            <View style={styles.editActions}>
              <TouchableOpacity disabled={saving} onPress={() => void saveChanges()} style={styles.primaryButton}>
                <Text style={styles.primaryButtonText}>{saving ? "저장 중..." : "저장"}</Text>
              </TouchableOpacity>
              <TouchableOpacity disabled={saving} onPress={cancelEdit} style={styles.secondaryButton}>
                <Text style={styles.secondaryButtonText}>취소</Text>
              </TouchableOpacity>
            </View>
          </View>
        ) : (
          <>
            <Text style={styles.subject}>{ticket.subject}</Text>
            <TicketStatusBadge status={ticket.status} />
            <Text style={styles.meta}>상담 구분: {ticket.categoryName}</Text>
            <Text style={styles.meta}>접수 일시: {formatDateTime(ticket.createdAt)}</Text>
            {canEdit ? (
              <TouchableOpacity onPress={() => setEditing(true)} style={styles.editButton}>
                <Text style={styles.editButtonText}>문의 수정</Text>
              </TouchableOpacity>
            ) : null}
          </>
        )}
      </View>

      {!editing ? (
        <>
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>문의 내용</Text>
            <Text style={styles.body}>{ticket.inquiryContent || "문의 내용이 없습니다."}</Text>
          </View>

          <TicketAttachmentSection canUpload={canUploadAttachment} ticketId={ticket.ticketId} />

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>상담 메시지</Text>
            {messages.length > 0 ? (
              <View style={styles.messageList}>
                {messages.map((message) => (
                  <View key={message.messageId} style={styles.messageItem}>
                    <Text style={styles.messageMeta}>
                      {formatSenderType(message.senderType)} · {formatDateTime(message.createdAt)}
                    </Text>
                    <Text style={styles.messageBody}>{message.content}</Text>
                  </View>
                ))}
              </View>
            ) : (
              <Text style={styles.hint}>아직 등록된 상담 메시지가 없습니다.</Text>
            )}
          </View>

          <View style={styles.section}>
            <Text style={styles.sectionTitle}>처리 결과</Text>
            <Text style={styles.body}>
              {ticket.resolution?.trim() ? ticket.resolution : "처리 결과가 등록되면 이곳에 표시됩니다."}
            </Text>
            {ticket.closedAt ? <Text style={styles.meta}>종료 일시: {formatDateTime(ticket.closedAt)}</Text> : null}
          </View>
        </>
      ) : null}
    </Screen>
  );
}

function Field({
  label,
  value,
  onChangeText,
  multiline,
  keyboardType = "default"
}: {
  label: string;
  value: string;
  onChangeText: (value: string) => void;
  multiline?: boolean;
  keyboardType?: "default" | "email-address";
}) {
  return (
    <View style={styles.field}>
      <Text style={styles.fieldLabel}>{label}</Text>
      <TextInput
        keyboardType={keyboardType}
        multiline={multiline}
        onChangeText={onChangeText}
        style={[styles.fieldInput, multiline && styles.textarea]}
        value={value}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  headerCard: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 12,
    borderWidth: 1,
    gap: 10,
    padding: 16
  },
  ticketNo: {
    color: "#71717a",
    fontSize: 12,
    fontWeight: "700"
  },
  subject: {
    color: "#09090b",
    fontSize: 20,
    fontWeight: "900"
  },
  meta: {
    color: "#52525b",
    fontSize: 13
  },
  section: {
    backgroundColor: "#ffffff",
    borderColor: "#e4e4e7",
    borderRadius: 12,
    borderWidth: 1,
    gap: 8,
    padding: 16
  },
  sectionTitle: {
    color: "#09090b",
    fontSize: 16,
    fontWeight: "900"
  },
  body: {
    color: "#27272a",
    fontSize: 14,
    lineHeight: 22
  },
  hint: {
    color: "#71717a",
    fontSize: 14
  },
  messageList: {
    gap: 10
  },
  messageItem: {
    borderTopColor: "#f4f4f5",
    borderTopWidth: 1,
    gap: 4,
    paddingTop: 10
  },
  messageMeta: {
    color: "#71717a",
    fontSize: 12,
    fontWeight: "700"
  },
  messageBody: {
    color: "#27272a",
    fontSize: 14,
    lineHeight: 20
  },
  editButton: {
    alignSelf: "flex-start",
    backgroundColor: "#ecfdf5",
    borderColor: "#0f766e",
    borderRadius: 8,
    borderWidth: 1,
    marginTop: 4,
    paddingHorizontal: 12,
    paddingVertical: 8
  },
  editButtonText: {
    color: "#0f766e",
    fontSize: 13,
    fontWeight: "800"
  },
  editForm: {
    gap: 12
  },
  editActions: {
    gap: 8
  },
  field: {
    gap: 6
  },
  fieldLabel: {
    color: "#3f3f46",
    fontSize: 14,
    fontWeight: "800"
  },
  fieldInput: {
    borderColor: "#d4d4d8",
    borderRadius: 8,
    borderWidth: 1,
    color: "#09090b",
    fontSize: 15,
    minHeight: 46,
    paddingHorizontal: 12
  },
  textarea: {
    minHeight: 120,
    paddingTop: 12,
    textAlignVertical: "top"
  },
  primaryButton: {
    alignItems: "center",
    backgroundColor: "#0f766e",
    borderRadius: 8,
    minHeight: 44,
    justifyContent: "center"
  },
  primaryButtonText: {
    color: "#ffffff",
    fontSize: 14,
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
  retryButton: {
    alignItems: "center",
    alignSelf: "flex-start",
    backgroundColor: "#ffffff",
    borderColor: "#d4d4d8",
    borderRadius: 8,
    borderWidth: 1,
    marginTop: 8,
    paddingHorizontal: 14,
    paddingVertical: 10
  },
  retryText: {
    color: "#27272a",
    fontSize: 14,
    fontWeight: "800"
  }
});
