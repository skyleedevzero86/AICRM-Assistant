UPDATE users
SET
    password_hash = '$2b$10$CioBHaSTHJxgj1V4TpVJ9OyQGd7cIKOVBsVPGRUr4NS7u7op0By5a',
    suspended_yn = 'N',
    withdrawn_yn = 'N',
    updated_at = now()
WHERE email IN (
    'admin@aicrm.local',
    'agent1@aicrm.local',
    'agent2@aicrm.local',
    'customer@example.com'
);

UPDATE agents a
SET status = 'ACTIVE', updated_at = now()
FROM users u
WHERE a.user_id = u.id
  AND u.email IN ('agent1@aicrm.local', 'agent2@aicrm.local');
