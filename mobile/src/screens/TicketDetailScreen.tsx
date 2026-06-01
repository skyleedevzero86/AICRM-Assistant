import { useEffect, useState } from "react";
import { StyleSheet, Text, View } from "react-native";
import { fetchCustomerTicket, fetchCustomerTicketMessages } from "@/api/customer";
import { ApiError } from "@/api/client";
import type { CustomerTicketDetail, Message } from "@/api/types";
import { EmptyState } from "@/components/EmptyState";
import { ErrorMessage } from "@/components/ErrorMessage";
import { LoadingView } from "@/components/LoadingView";
import { Screen } from "@/components/Screen";
import { TicketStatusBadge } from "@/components/TicketStatusBadge";
import { formatDateTime, formatSenderType } from "@/utils/format";

export function TicketDetailScreen({ ticketId }: { ticketId: number }) {
  const [ticket, setTicket] = useState<CustomerTicketDetail | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [loading, setLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    setLoading(true);
    setErrorMessage(null);

    Promise.all([fetchCustomerTicket(ticketId), fetchCustomerTicketMessages(ticketId)])
      .then(([detail, history]) => {
        if (!active) return;
        setTicket(detail);
        setMessages(history.filter((message) => message.messageType === "TEXT"));
      })
      .catch((error) => {
        if (!active) return;
        setTicket(null);
        setMessages([]);
        setErrorMessage(error instanceof ApiError ? error.message : "문의 상세를 불러오지 못했습니다.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [ticketId]);

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

  return (
    <Screen>
      <View style={styles.headerCard}>
        <Text style={styles.ticketNo}>{ticket.ticketNo}</Text>
        <Text style={styles.subject}>{ticket.subject}</Text>
        <TicketStatusBadge status={ticket.status} />
        <Text style={styles.meta}>상담 구분: {ticket.categoryName}</Text>
        <Text style={styles.meta}>접수 일시: {formatDateTime(ticket.createdAt)}</Text>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>문의 내용</Text>
        <Text style={styles.body}>{ticket.inquiryContent || "문의 내용이 없습니다."}</Text>
      </View>

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
    </Screen>
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
  }
});
