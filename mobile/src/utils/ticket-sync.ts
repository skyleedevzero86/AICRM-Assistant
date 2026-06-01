import { fetchCustomerTickets } from "@/api/customer";
import type { StoredTicket } from "@/storage/ticketStorage";

export async function refreshStoredTickets(stored: StoredTicket[]): Promise<StoredTicket[]> {
  if (stored.length === 0) return [];

  try {
    const remote = await fetchCustomerTickets();
    const remoteById = new Map(remote.map((ticket) => [ticket.ticketId, ticket]));

    return stored.map((item) => {
      const latest = remoteById.get(item.ticketId);
      if (!latest) return item;
      return {
        ...item,
        status: latest.status,
        title: latest.subject,
        categoryName: latest.categoryName,
        createdAt: latest.createdAt
      };
    });
  } catch {
    return stored;
  }
}
