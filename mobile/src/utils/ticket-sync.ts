import { fetchCustomerTickets } from "@/api/customer";
import type { CustomerTicketSummary } from "@/api/types";
import { clearStoredTickets, replaceStoredTickets, type StoredTicket } from "@/storage/ticketStorage";

export function toStoredTicket(ticket: CustomerTicketSummary): StoredTicket {
  return {
    ticketId: ticket.ticketId,
    ticketNo: ticket.ticketNo,
    title: ticket.subject,
    status: ticket.status,
    createdAt: ticket.createdAt,
    categoryName: ticket.categoryName
  };
}

export async function loadCustomerTickets(): Promise<CustomerTicketSummary[]> {
  const tickets = await fetchCustomerTickets();
  await replaceStoredTickets(tickets.map(toStoredTicket));
  return tickets;
}
