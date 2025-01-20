CREATE TYPE OPERATION_TYPE AS ENUM (
    'MOVE',
    'EDIT'
); -- изменение

CREATE TYPE RESOURCE_PARAMETER AS ENUM (
    'COUNT'
);

CREATE TABLE move_operation(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quantity INT NOT NULL,
    from_user UUID NOT NULL REFERENCES company_user(id),
    to_user UUID NOT NULL REFERENCES company_user(id),
    resource UUID NOT NULL REFERENCES resource(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    applied BOOLEAN NOT NULL DEFAULT FALSE,
    applied_at TIMESTAMPTZ
);

CREATE TABLE edit_operation(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    quantity INT NOT NULL,
    user UUID NOT NULL REFERENCES company_user(id),
    resource UUID NOT NULL REFERENCES resource(id),

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- TODO: implement all possible operations

-- перемещение: какой сколько откуда куда
