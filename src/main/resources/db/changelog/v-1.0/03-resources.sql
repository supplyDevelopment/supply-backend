CREATE TYPE INVENTORY_ITEM_STATUS AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'REPAIR',
    'DELIVER'
);

CREATE TABLE resource (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(1024) NOT NULL,
    description VARCHAR(1024),
    status      INVENTORY_ITEM_STATUS,
    count       INTEGER NOT NULL default 1,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE resource_operations (

);