ALTER TABLE tickets
    ADD COLUMN ticket_no VARCHAR(30);

UPDATE tickets
SET ticket_no = 'TICKET-' || to_char(created_at AT TIME ZONE 'UTC', 'YYYYMMDD') || '-' || lpad(id::text, 4, '0')
WHERE ticket_no IS NULL;

ALTER TABLE tickets
    ALTER COLUMN ticket_no SET NOT NULL;

CREATE UNIQUE INDEX idx_tickets_ticket_no ON tickets(ticket_no);

CREATE INDEX idx_customers_phone ON customers(phone);
