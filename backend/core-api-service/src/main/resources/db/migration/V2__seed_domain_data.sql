INSERT INTO consultation_categories (code, name, description, sort_order) VALUES
    ('GENERAL', 'General Inquiry', 'General customer inquiries', 1),
    ('BILLING', 'Billing', 'Billing and payment issues', 2),
    ('TECHNICAL', 'Technical Support', 'Technical support requests', 3),
    ('COMPLAINT', 'Complaints', 'Customer complaints', 4);

INSERT INTO users (email, password_hash, name) VALUES
    ('agent1@aicrm.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Agent One'),
    ('agent2@aicrm.local', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Agent Two'),
    ('customer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Kim Customer');

INSERT INTO agents (user_id, name, status) VALUES
    (1, 'Agent One', 'ACTIVE'),
    (2, 'Agent Two', 'ACTIVE');

INSERT INTO customers (user_id, name, phone, email) VALUES
    (3, 'Kim Customer', '010-1234-5678', 'customer@example.com'),
    (NULL, 'Lee Guest', '010-9876-5432', 'guest@example.com');

INSERT INTO tickets (customer_id, agent_id, category_id, status, channel, subject, assigned_at) VALUES
    (1, NULL, 1, 'WAITING', 'WEB_INQUIRY', 'Account login issue', NULL),
    (2, 1, 2, 'ASSIGNED', 'EMAIL', 'Billing statement question', now()),
    (1, 1, 3, 'IN_PROGRESS', 'WEB_CHAT', 'App crash on startup', now());

INSERT INTO conversations (ticket_id, channel) VALUES
    (1, 'WEB_INQUIRY'),
    (2, 'EMAIL'),
    (3, 'WEB_CHAT');

INSERT INTO messages (conversation_id, sender_type, sender_id, content) VALUES
    (1, 'CUSTOMER', 1, 'I cannot log in to my account since yesterday.'),
    (1, 'SYSTEM', NULL, 'Your inquiry has been received. An agent will respond shortly.'),
    (2, 'CUSTOMER', 2, 'Could you explain the extra charge on my latest bill?'),
    (2, 'AGENT', 1, 'I am reviewing your billing history and will update you soon.'),
    (3, 'CUSTOMER', 1, 'The mobile app crashes immediately after launch.'),
    (3, 'AI', NULL, 'Try clearing the app cache and reinstalling the latest version.'),
    (3, 'AGENT', 1, 'I escalated this to our mobile team for further investigation.');
