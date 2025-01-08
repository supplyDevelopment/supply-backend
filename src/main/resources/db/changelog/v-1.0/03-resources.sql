CREATE TYPE INVENTORY_ITEM_STATUS AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'REPAIR',
    'DELIVER'
);

CREATE TYPE RESOURCE_TYPE AS ENUM (
    'TOOL',
    'PRODUCT'
);

CREATE TABLE project (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(1024) NOT NULL,
    description VARCHAR(1024),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE resource (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    images      VARCHAR[],
    name        VARCHAR(1024) NOT NULL,
    count       INTEGER NOT NULL default 1,
    unit        UNIT NOT NULL,
    type        RESOURCE_TYPE NOT NULL,
    projectId   UUID NOT NULL REFERENCES project(id),
    status      INVENTORY_ITEM_STATUS NOT NULL,
    description VARCHAR(1024),
    created_at  TIMESTAMPTZ NOT NULL,
    updated_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE resource_users (
    resource_id  UUID PRIMARY KEY REFERENCES resource(id),
    user_id      UUID NOT NULL REFERENCES company_user(id)
);

CREATE TABLE resource_operations (

);

