UPDATE users
SET
    password_hash = '$2b$10$CioBHaSTHJxgj1V4TpVJ9OyQGd7cIKOVBsVPGRUr4NS7u7op0By5a',
    role = 'ADMIN',
    suspended_yn = 'N',
    withdrawn_yn = 'N',
    updated_at = now()
WHERE email = 'admin@aicrm.local';

INSERT INTO users (email, password_hash, name, role, suspended_yn, withdrawn_yn)
SELECT
    'admin@aicrm.local',
    '$2b$10$CioBHaSTHJxgj1V4TpVJ9OyQGd7cIKOVBsVPGRUr4NS7u7op0By5a',
    'Admin User',
    'ADMIN',
    'N',
    'N'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@aicrm.local'
);
