CREATE TABLE warehouse (
   id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   name          VARCHAR(255) NOT NULL,
   location      VARCHAR(1024) NOT NULL,
   company_id    UUID NOT NULL REFERENCES company(id),
   stock_level   INTEGER NOT NULL DEFAULT 0,
   capacity      INTEGER NOT NULL,
   created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   updated_at    TIMESTAMPTZ
);
