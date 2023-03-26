delete from city;
delete from vehicle_type;
delete from weather_data;

INSERT INTO city (city, fee) VALUES
                                 ('Tallinn-Harku', 2.5),
                                 ('Tartu-Tõravere', 2.0),
                                 ('Pärnu', 1.5);
INSERT INTO vehicle_type (vehicle, fee) VALUES
                                            ('Car', 1.5),
                                            ('Scooter', 1.0),
                                            ('Bike', 0.5);
INSERT INTO weather_data (station_name, wmo_code, air_temperature, wind_speed, weather_phenomenon, timestamp)
VALUES ('Tallinn-Harku', '26038', -2.1, 4.7, 'Moderate rain', '2023-03-26T09:37:03Z'),
       ('Tartu-Tõravere', '26242', -2.1, 4.7, 'Light snow shower', '2023-03-26T09:37:03Z'),
       ('Pärnu', '41803', -2.1, 4.7, 'Overcast', '2023-03-26T09:37:03Z');