drop table if exists car_make;

create table car_make (
    id serial primary key,
    name varchar(255) not null
);

insert into car_make (name) values
    ('Toyota'),
    ('Honda'),
    ('Ford'),
    ('Chevrolet'),
    ('Nissan');

-- =============================================================================

drop table if exists car_model;

create table car_model (
    id serial primary key,
    name varchar(255) not null,
    make_id int not null,
    year int not null,
    price numeric not null
);

insert into car_model (name, make_id, year, price) values
    ('Camry',   1, 2020, 24000),
    ('Civic',   2, 2021, 22000),
    ('Mustang', 3, 2019, 26000),
    ('Malibu',  4, 2020, 23000),
    ('Altima',  5, 2021, 25000);

-- =============================================================================

drop table if exists car_part;

create table car_part (
    id serial primary key,
    name varchar(255) not null,
    price numeric(9, 2) not null
);

insert into car_part (name, price) values
    ('Engine',       5000.00),
    ('Transmission', 3000.00),
    ('Brakes',       1500.00),
    ('Tires',         800.00),
    ('Battery',       200.00),
    ('Alternator',    600.00),
    ('Clip',            0.25);

-- =============================================================================

drop table if exists car_model_part;

create table car_model_part (
    model_id integer not null,
    part_id integer not null,
    primary key (model_id, part_id)
);

insert into car_model_part (model_id, part_id) values
    (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 2),
    (2, 4),
    (3, 1),
    (3, 5),
    (4, 6),
    (5, 7);
