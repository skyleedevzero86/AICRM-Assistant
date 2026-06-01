import { useLocalSearchParams } from "expo-router";
import { TicketDetailScreen } from "@/screens/TicketDetailScreen";

export default function TicketDetailRoute() {
  const { ticketId } = useLocalSearchParams<{ ticketId: string }>();
  const id = Number(ticketId);

  if (!Number.isFinite(id)) {
    return null;
  }

  return <TicketDetailScreen ticketId={id} />;
}
