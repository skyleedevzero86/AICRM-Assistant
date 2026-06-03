ALTER TABLE users
    ADD COLUMN IF NOT EXISTS withdrawn_yn CHAR(1) NOT NULL DEFAULT 'N',
    ADD COLUMN IF NOT EXISTS suspended_yn CHAR(1) NOT NULL DEFAULT 'N';

ALTER TABLE agents
    ADD COLUMN IF NOT EXISTS grade VARCHAR(30) NOT NULL DEFAULT 'COUNSELOR';

CREATE TABLE IF NOT EXISTS agent_attendance_daily (
    id BIGSERIAL PRIMARY KEY,
    agent_id BIGINT NOT NULL REFERENCES agents(id),
    work_date DATE NOT NULL,
    login_mark CHAR(1) NOT NULL DEFAULT 'X',
    login_count INTEGER NOT NULL DEFAULT 0,
    break_minutes INTEGER NOT NULL DEFAULT 0,
    work_minutes INTEGER NOT NULL DEFAULT 0,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (agent_id, work_date)
);

CREATE INDEX IF NOT EXISTS idx_agent_attendance_work_date ON agent_attendance_daily(work_date);
