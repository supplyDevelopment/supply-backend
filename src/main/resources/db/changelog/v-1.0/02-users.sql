CREATE TABLE company_user (
     id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     name        USER_NAME NOT NULL,
     password    VARCHAR(1024) NOT NULL,
     privileges  user_privilege[],
     email       EMAIL NOT NULL UNIQUE,
     phone       PHONE NOT NULL,
     created_at  TIMESTAMPTZ NOT NULL,
     updated_at  TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE supplier (
     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     name USER_NAME NOT NULL,
     password VARCHAR(1024),
     emails EMAIL[] NOT NULL,
     phones PHONE[] NOT NULL,
     address VARCHAR(1024) NOT NULL,
     created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
     updated_at TIMESTAMPTZ DEFAULT NOW()
);


