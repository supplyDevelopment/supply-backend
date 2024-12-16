CREATE TYPE COMPANY_STATUS AS ENUM (
    'ACTIVE',
    'INACTIVE'
);

CREATE TABLE company(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(1024) NOT NULL UNIQUE,
    contact_emails EMAIL[] NOT NULL,
    contact_phones PHONE[] NOT NULL,
    bil_address VARCHAR(128) NOT NULL UNIQUE,
    tax_id VARCHAR(128) NOT NULL UNIQUE,
    addresses VARCHAR(1024)[] NOT NULL,
    status COMPANY_STATUS NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ
);