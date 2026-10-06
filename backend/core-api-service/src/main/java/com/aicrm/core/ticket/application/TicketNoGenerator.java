package com.aicrm.core.ticket.application;

import com.aicrm.core.ticket.domain.TicketRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class TicketNoGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final TicketRepository ticketRepository;

    public TicketNoGenerator(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public String generateNext() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant start = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = today.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        long count = ticketRepository.countByCreatedAtBetween(start, end);
        return "TICKET-" + today.format(DATE_FORMAT) + "-" + String.format("%04d", count + 1);
    }
}
