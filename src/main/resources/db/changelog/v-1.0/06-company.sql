CREATE TYPE COMPANY_STATUS AS ENUM (
    'ACTIVE',
    'INACTIVE'
);

CREATE TABLE company(
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(1024) NOT NULL UNIQUE,
    contact_emails  EMAIL[] NOT NULL,
    contact_phones  PHONE[] NOT NULL,
    bil_address     VARCHAR(128) NOT NULL UNIQUE,
    tax_id          VARCHAR(128) NOT NULL UNIQUE,
    addresses       VARCHAR(1024)[] NOT NULL,
    status          COMPANY_STATUS NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE company_warehouses(
    warehouse   UUID NOT NULL REFERENCES warehouse(id),
    company     UUID NOT NULL REFERENCES company(id)
);

CREATE TABLE company_users(
    user_id      UUID NOT NULL REFERENCES company_user(id),
    company_id   UUID NOT NULL REFERENCES company(id)
);

CREATE TABLE company_suppliers(
    supplier_id  UUID NOT NULL REFERENCES supplier(id),
    company_id   UUID NOT NULL REFERENCES company(id)
);