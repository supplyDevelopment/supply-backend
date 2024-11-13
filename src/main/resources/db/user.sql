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
