import AsyncStorage from "@react-native-async-storage/async-storage";
import type { TicketStatus } from "@/api/types";

export type StoredTicket = {
  ticketId: number;
  ticketNo: string;
  title: string;
  status: TicketStatus;
  createdAt: string;
  categoryName?: string;
};

const STORAGE_KEY = "aicrm-stored-tickets";
const MAX_ITEMS = 50;

export async function loadStoredTickets(): Promise<StoredTicket[]> {
  try {
    const raw = await AsyncStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw) as StoredTicket[];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export async function saveStoredTicket(ticket: StoredTicket): Promise<StoredTicket[]> {
  const current = await loadStoredTickets();
  const next = [ticket, ...current.filter((item) => item.ticketId !== ticket.ticketId)].slice(0, MAX_ITEMS);
  await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(next));
  return next;
}

export async function clearStoredTickets(): Promise<void> {
  await AsyncStorage.removeItem(STORAGE_KEY);
}

export async function replaceStoredTickets(tickets: StoredTicket[]): Promise<StoredTicket[]> {
  await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(tickets.slice(0, MAX_ITEMS)));
  return tickets;
}

export async function loadAndRefreshTickets(
  refresh: (stored: StoredTicket[]) => Promise<StoredTicket[]>
): Promise<StoredTicket[]> {
  const stored = await loadStoredTickets();
  const refreshed = await refresh(stored);
  if (refreshed.length > 0) {
    await replaceStoredTickets(refreshed);
  }
  return refreshed;
}
