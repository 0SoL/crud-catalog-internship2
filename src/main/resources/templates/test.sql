create sequence catalog_entity_seq start with 1 increment by 50;

CREATE TABLE IF NOT EXISTS catalog (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    price NUMERIC(10,2) NOT NULL,
    name VARCHAR(100),
    description VARCHAR(255)
    );

create table image (
       id integer generated always as identity primary key ,
       is_primary boolean not null,
       url varchar(255),
       catalog_id integer
)