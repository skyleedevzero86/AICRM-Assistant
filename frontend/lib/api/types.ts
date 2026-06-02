export type ApiResponse<T> = {
  success: boolean;
  data: T;
  error: { code: string; message: string } | null;
};

export type UserRole = "CUSTOMER" | "AGENT" | "ADMIN";

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  userId: number;
  email: string;
  role: UserRole;
};

export type SignUpRequest = {
  name: string;
  email: string;
  password: string;
};

export type SignUpResponse = {
  userId: number;
  email: string;
  role: UserRole;
  signupStatus: "ACTIVE" | "PENDING";
};

export type MeResponse = {
  userId: number;
  email: string;
  name: string;
  role: UserRole;
};

export type TicketStatus =
  | "WAITING"
  | "ASSIGNED"
  | "IN_PROGRESS"
  | "RESOLVED"
  | "CLOSED"
  | "ESCALATED";

export type ChannelType = "WEB_INQUIRY" | "WEB_CHAT" | "PHONE" | "EMAIL";

export type SenderType = "CUSTOMER" | "AGENT" | "AI" | "SYSTEM";

export type MessageType = "TEXT" | "INTERNAL_MEMO" | "AI_DRAFT";

export type ConsultationCategoryTreeNode = {
  id: number;
  code: string;
  name: string;
  depth: number;
  children: ConsultationCategoryTreeNode[];
};

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

export type WaitingTicket = {
  ticketId: number;
  ticketNo: string;
  subject: string;
  customerName: string;
  categoryName: string;
  channel: ChannelType;
  createdAt: string;
};

export type AgentTicketDetail = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
  subject: string;
  channel: ChannelType;
  createdAt: string;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  categoryId: number;
  categoryName: string;
  inquiryContent: string;
};

export type AcceptTicketResponse = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
  agentId: number;
};

export type CloseTicketRequest = {
  resolution: string;
};

export type CloseTicketResponse = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
  resolution: string;
  closedAt: string;
  conversationEndedAt: string;
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
