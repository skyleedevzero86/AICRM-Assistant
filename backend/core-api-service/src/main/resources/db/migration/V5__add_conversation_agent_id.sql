ALTER TABLE conversations
    ADD COLUMN agent_id BIGINT REFERENCES agents(id);

CREATE INDEX idx_conversations_agent_id ON conversations(agent_id);
