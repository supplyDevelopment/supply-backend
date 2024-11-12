CREATE TABLE company_user(
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        USER_NAME NOT NULL,
    password    VARCHAR(2048) NOT NULL,
    privileges  user_privilege[],
    email       EMAIL NOT NULL UNIQUE,
    phone       PHONE NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ
);

CREATE TABLE company_user(
    userId      UUID NOT NULL REFERENCES company_user(id),
    companyId   UUID NOT NULL REFERENCES company(id)
);

CREATE TABLE warehouse_user(
    userId      UUID NOT NULL REFERENCES company_user(id),
    warehouse   UUID NOT NULL REFERENCES warehouse(id)
);


