CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO categories (name, description) VALUES
    ('GENERAL', 'General inquiry'),
    ('BILLING', 'Billing and payment'),
    ('TECHNICAL', 'Technical support'),
    ('COMPLAINT', 'Customer complaint');
