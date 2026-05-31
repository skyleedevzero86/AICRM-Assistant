package com.aicrm.core.ticket.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.aicrm.core.ticket.domain.TicketRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketNoGeneratorTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketNoGenerator ticketNoGenerator;

    @Test
    void generateNextReturnsFormattedTicketNo() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant start = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = today.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        when(ticketRepository.countByCreatedAtBetween(start, end)).thenReturn(0L);

        String ticketNo = ticketNoGenerator.generateNext();

        assertThat(ticketNo).isEqualTo("TICKET-" + today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-0001");
    }

    @Test
    void generateNextIncrementsSequenceForSameDay() {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Instant start = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = today.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
        when(ticketRepository.countByCreatedAtBetween(start, end)).thenReturn(5L);

        String ticketNo = ticketNoGenerator.generateNext();

        assertThat(ticketNo).isEqualTo("TICKET-" + today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-0006");
    }
}
