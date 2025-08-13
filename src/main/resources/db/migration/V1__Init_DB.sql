create sequence catalog_entity_seq start with 1 increment by 50;

CREATE TABLE IF NOT EXISTS catalog_entity (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    price NUMERIC(10,2) NOT NULL,
    name VARCHAR(100),
    description VARCHAR(255)
    );