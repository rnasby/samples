drop table if exists t_car_make;
drop table if exists t_car_model;
drop table if exists t_car_part;
drop table if exists t_car_model_part;

create table t_car_make (id integer identity primary key, name varchar(255) not null);
create table t_car_model (id integer identity primary key, name varchar(255) not null, make_id int not null,
                          year int not null, price numeric not null);
create table t_car_part (id integer identity primary key, name varchar(255) not null, price numeric(9, 2) not null);
create table t_car_model_part (id integer identity primary key, model_id integer not null, part_id integer not null);
