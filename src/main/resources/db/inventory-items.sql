CREATE TYPE INVENTORY_ITEM_STATUS AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'REPAIR',
    'DELIVER'
);

CREATE TABLE tool (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(1024) NOT NULL,
    description VARCHAR(1024),
    status INVENTORY_ITEM_STATUS NOT NULL,
    serial_number VARCHAR(1024) NOT NULL
);

CREATE TABLE product(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(1024) NOT NULL,
    description VARCHAR(1024),
    count INT NOT NULL,
    status INVENTORY_ITEM_STATUS NOT NULL,
    expiration_date DATE
)

-- @TODO: create connection to operations
-- @TODO: create connection to warehouse
-- @TODO: create connection to users
-- @TODO: create connection to supplier