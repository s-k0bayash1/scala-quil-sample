BEGIN;

CREATE SCHEMA hogeschema;

SET
SEARCH_PATH TO hogeschema;

CREATE TABLE person
(
    id    UUID PRIMARY KEY,
    name  VARCHAR NOT NULL,
    age   INTEGER NOT NULL,
    hobby VARCHAR
);

CREATE TYPE CATEGORY AS ENUM ('something1','something2');
CREATE CAST (CHARACTER VARYING AS CATEGORY) WITH inout AS ASSIGNMENT;
CREATE TABLE item
(
    id        SERIAL PRIMARY KEY,
    person_id UUID REFERENCES person (id) ON DELETE CASCADE,
    name      VARCHAR  NOT NULL,
    category  CATEGORY NOT NULL,
    CONSTRAINT item_uk UNIQUE (name, category)
);

CREATE TABLE item_detail
(
    name        VARCHAR NOT NULL,
    description VARCHAR,
    item_id     SERIAL REFERENCES item (id) ON DELETE CASCADE,
--     NOTE: ARRAY型はjava.sql.Arrayで生成されて扱いづらい
    str_array   VARCHAR[],
    PRIMARY KEY (name, item_id)
);

COMMIT;
