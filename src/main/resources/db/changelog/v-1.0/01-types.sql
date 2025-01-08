CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE DOMAIN ONLY_LETTERS AS VARCHAR
    CHECK (
        VALUE ~ '^[a-zA-Zа-яА-Я]+$'
    );

CREATE TYPE USER_NAME as (
    first_name ONLY_LETTERS,
    second_name ONLY_LETTERS,
    last_name ONLY_LETTERS
);

CREATE TYPE user_privilege AS ENUM (
    'READ',
    'WRITE',
    'DELETE',
    'ADMIN'
);

CREATE DOMAIN EMAIL AS VARCHAR
    CHECK (
        VALUE ~ '^[\w\-\.]+@([\w-]+\.)+[\w-]{2,4}$'
    );

CREATE DOMAIN PHONE AS VARCHAR
    CHECK (
        VALUE ~ '^\+7\d{10}$'
    );

CREATE TYPE UNIT AS ENUM (
    'KG'
); -- грамы литры милилитры штуки куб см