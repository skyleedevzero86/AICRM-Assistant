ALTER TABLE users
    ADD COLUMN IF NOT EXISTS role VARCHAR(30) NOT NULL DEFAULT 'CUSTOMER';

UPDATE users
SET role = 'AGENT'
WHERE id IN (
    SELECT user_id
    FROM agents
);

INSERT INTO users (email, password_hash, name, role)
SELECT 'admin@aicrm.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin User', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@aicrm.local'
);

INSERT INTO users (email, password_hash, name, role)
SELECT 'agent-pending@aicrm.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Agent Pending', 'AGENT'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'agent-pending@aicrm.local'
);

INSERT INTO agents (user_id, name, status)
SELECT u.id, 'Agent Pending', 'PENDING'
FROM users u
WHERE u.email = 'agent-pending@aicrm.local'
  AND NOT EXISTS (
      SELECT 1 FROM agents a WHERE a.user_id = u.id
  );

UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@aicrm.local';
