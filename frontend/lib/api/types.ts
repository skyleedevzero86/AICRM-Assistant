export type ApiResponse<T> = {
  success: boolean;
  data: T;
  error: { code: string; message: string } | null;
};

export type UserRole = "CUSTOMER" | "AGENT" | "ADMIN";
export type AgentGrade = "ADMIN" | "COUNSELOR" | "TEAM_LEAD";
export type AgentApprovalStatus = "PENDING" | "ACTIVE";

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

export type AgentSignUpRequest = SignUpRequest & {
  employeeNo: string;
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
  phone: string;
  employeeNo: string;
};

export type UpdateMeRequest = {
  name?: string;
  password?: string;
  phone?: string;
};

export type AdminCustomerUser = {
  userId: number;
  name: string;
  email: string;
  phone: string;
  withdrawnYn: "Y" | "N";
  suspendedYn: "Y" | "N";
};

export type AdminAgentUser = {
  userId: number;
  agentId: number;
  employeeNo: string;
  name: string;
  email: string;
  approvalStatus: AgentApprovalStatus;
  grade: AgentGrade;
  withdrawnYn: "Y" | "N";
  suspendedYn: "Y" | "N";
};

export type AdminAgentAttendance = {
  agentId: number;
  agentName: string;
  email: string;
  grade: AgentGrade;
  workDate: string;
  loginMark: "0" | "X";
  loginCount: number;
  breakMinutes: number;
  workMinutes: number;
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

export type CustomerTicketSummary = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
  subject: string;
  categoryName: string;
  createdAt: string;
};

export type CustomerTicketDetail = CustomerTicketSummary & {
  categoryId: number;
  customerName: string;
  customerPhone: string;
  customerEmail: string;
  inquiryContent: string;
  resolution: string | null;
  closedAt: string | null;
};

export type UpdateCustomerInquiryRequest = {
  customerName: string;
  email: string;
  categoryId: number;
  title: string;
  content: string;
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
  agentId: number | null;
  resolution: string | null;
  closedAt: string | null;
};

export type AgentTicketSummary = {
  ticketId: number;
  ticketNo: string;
  status: TicketStatus;
  subject: string;
  customerName: string;
  categoryName: string;
  channel: ChannelType;
  createdAt: string;
  closedAt: string | null;
};

export type AdminTicketSummary = AgentTicketSummary & {
  agentId: number | null;
  agentName: string | null;
  agentEmployeeNo: string | null;
};

export type AdminTicketDetail = AgentTicketDetail & {
  agentId: number | null;
  agentName: string | null;
  agentEmployeeNo: string | null;
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
