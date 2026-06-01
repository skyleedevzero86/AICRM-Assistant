CREATE TYPE message_type AS ENUM (
    'TEXT',
    'INTERNAL_MEMO',
    'AI_DRAFT'
);

ALTER TABLE messages
    ADD COLUMN message_type message_type NOT NULL DEFAULT 'TEXT';
