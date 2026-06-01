import { fetchCustomerTickets } from "@/api/customer";
import type { CustomerTicketSummary } from "@/api/types";
import type { StoredTicket } from "@/storage/ticketStorage";
import { loadStoredTickets, replaceStoredTickets } from "@/storage/ticketStorage";

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

export async function syncTicketsFromApi(): Promise<StoredTicket[]> {
  const remote = await fetchCustomerTickets();
  const tickets = remote.map(toStoredTicket);
  await replaceStoredTickets(tickets);
  return tickets;
}

export async function loadTicketsWithFallback(): Promise<StoredTicket[]> {
  try {
    return await syncTicketsFromApi();
  } catch {
    return loadStoredTickets();
  }
}
