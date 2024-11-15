
CREATE TABLE supplier(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(1024) NOT NULL,
    contact_emails EMAIL[] NOT NULL,
    contact_phones PHONE[] NOT NULL,
    address VARCHAR(1024) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);

-- TODO: create connection to operations
-- TODO: create connection to company
-- TODO: create connection to tenders