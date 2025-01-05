CREATE TABLE warehouse (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name          VARCHAR(255) NOT NULL,
    location      VARCHAR(1024) NOT NULL,
    stock_level   INTEGER NOT NULL DEFAULT 0,
    capacity      INTEGER NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE warehouse_admins (
    user_id         UUID NOT NULL REFERENCES company_user(id),
    warehouse_id    UUID NOT NULL REFERENCES warehouse(id)
);

CREATE TABLE warehouse_resources (
    tool_id         UUID NOT NULL REFERENCES resource(id),
    warehouse_id    UUID NOT NULL REFERENCES warehouse(id)
);