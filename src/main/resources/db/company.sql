CREATE TYPE STATUS AS ENUM (
    'ACTIVE',
    'INACTIVE'
);

CREATE TABLE company(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(1024) NOT NULL,
    admin_id    UUID NOT NULL,
    contact_emails EMAIL[] NOT NULL,
    contact_phones PHONE[] NOT NULL,
    bil_address VARCHAR(128) NOT NULL UNIQUE,
    tax_id VARCHAR(128) NOT NULL UNIQUE,
    addresses VARCHAR(1024)[] NOT NULL,
    status STATUS NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ
);

CREATE TABLE warehouse_company(
    warehouse   UUID NOT NULL REFERENCES warehouse(id),
    company     UUID NOT NULL REFERENCES company(id)
);