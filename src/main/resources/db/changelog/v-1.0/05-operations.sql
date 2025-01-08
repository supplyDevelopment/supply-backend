CREATE TYPE OPERATION_TYPE AS ENUM (
    'ADD',
    'REMOVE',
    'REPLACE'
); -- изменение

CREATE TABLE operation(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type OPERATION_TYPE NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- TODO: implement all possible operations

-- перемещение: какой сколько откуда куда
