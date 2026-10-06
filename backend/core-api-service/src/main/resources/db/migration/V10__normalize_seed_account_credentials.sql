UPDATE users
SET
    password_hash = '$2b$10$CioBHaSTHJxgj1V4TpVJ9OyQGd7cIKOVBsVPGRUr4NS7u7op0By5a',
    suspended_yn = 'N',
    withdrawn_yn = 'N'
WHERE email IN (
    'admin@aicrm.local',
    'agent1@aicrm.local',
    'agent-pending@aicrm.local',
    'customer@example.com'
);

UPDATE users
SET role = 'ADMIN'
WHERE email = 'admin@aicrm.local';
