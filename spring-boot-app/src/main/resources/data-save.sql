insert into t_car_make (name) values
                                ('Toyota'),
                                ('Honda'),
                                ('Ford'),
                                ('Chevrolet'),
                                ('Nissan');

insert into t_car_model (name, car_make_id, year, price) values
                               ('Camry',   0, 2020, 24000),
                               ('Civic',   1, 2021, 22000),
                               ('Mustang', 2, 2019, 26000),
                               ('Malibu',  3, 2020, 23000),
                               ('Altima',  4, 2021, 25000);

insert into t_car_part (name, price) values
                               ('Engine',       5000.00),
                               ('Transmission', 3000.00),
                               ('Brakes',       1500.00),
                               ('Tires',         800.00),
                               ('Battery',       200.00),
                               ('Alternator',    600.00),
                               ('Clip',            0.25);

insert into t_car_model_part (car_model_id, car_part_id) values
                               (0, 0),
                               (0, 1),
                               (0, 2),

                               (1, 0),
                               (1, 1),
                               (1, 3),

                               (2, 0),

                               (3, 4),
                               (3, 5),
                               (4, 6);
