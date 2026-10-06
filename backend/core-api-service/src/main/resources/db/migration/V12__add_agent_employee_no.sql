ALTER TABLE agents
    ADD COLUMN IF NOT EXISTS employee_no VARCHAR(16);

UPDATE agents a
SET employee_no = '20260101000000' || LPAD(a.id::text, 2, '0')
WHERE employee_no IS NULL;

ALTER TABLE agents
    ALTER COLUMN employee_no SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_agents_employee_no ON agents(employee_no);
