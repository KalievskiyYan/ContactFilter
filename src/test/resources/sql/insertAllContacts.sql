CREATE TABLE IF NOT EXISTS contacts (
   id bigint GENERATED ALWAYS AS IDENTITY,
   name varchar (255) NOT NULL
);

insert into contacts (name)
values ('Yan'),
       ('Ali'),
       ('Di'),
       ('Nike'),
       ('Alyo'),
       ('Versh'),
       ('Vi'),
       ('Vla');