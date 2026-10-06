ALTER TABLE tickets
    ADD COLUMN resolution TEXT;

ALTER TABLE conversations
    ADD COLUMN ended_at TIMESTAMPTZ;

CREATE INDEX idx_conversations_ended_at ON conversations(ended_at);
