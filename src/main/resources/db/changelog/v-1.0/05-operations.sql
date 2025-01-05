CREATE TYPE OPERATION_TYPE AS ENUM (
    'ADD',
    'REMOVE',
    'REPLACE'
);

CREATE TABLE operation(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type OPERATION_TYPE NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE comment(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    text VARCHAR(1024) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- TODO: implement all possible operations