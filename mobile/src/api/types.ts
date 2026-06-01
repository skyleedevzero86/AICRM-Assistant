export type TicketStatus = "WAITING" | "ASSIGNED" | "IN_PROGRESS" | "RESOLVED" | "CLOSED" | "ESCALATED";

export type ConsultationCategoryTreeNode = {
  id: number;
  code: string;
  name: string;
  depth: number;
  children: ConsultationCategoryTreeNode[];
};

export type SenderType = "CUSTOMER" | "AGENT" | "AI" | "SYSTEM";

export type MessageType = "TEXT" | "INTERNAL_MEMO" | "AI_DRAFT";

export type CreateCustomerInquiryRequest = {
  customerName: string;
  phone: string;
  email: string;
  categoryId: number;
  title: string;
  content: string;
};

export type CreateCustomerInquiryResponse = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
};

export type CustomerTicketSummary = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
  subject: string;
  categoryName: string;
  createdAt: string;
};

export type CustomerTicketDetail = CustomerTicketSummary & {
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  inquiryContent: string;
  resolution: string | null;
  closedAt: string | null;
};

export type Message = {
  messageId: number;
  ticketId: number;
  senderType: SenderType;
  senderId: number | null;
  messageType: MessageType;
  content: string;
  createdAt: string;
};

export type SaveMessageRequest = {
  content: string;
  messageType?: MessageType;
};

export type SaveMessageResponse = {
  messageId: number;
  ticketId: number;
  senderType: SenderType;
  messageType: MessageType;
  content: string;
  createdAt: string;
};
