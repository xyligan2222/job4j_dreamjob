CREATE TABLE post (
                      id SERIAL PRIMARY KEY,
                      name TEXT
);

CREATE TABLE candidate (
                      id SERIAL PRIMARY KEY,
                      name TEXT
);

CREATE TABLE photo (
                           id SERIAL PRIMARY KEY,
                           name TEXT
);
ALTER TABLE candidate ADD COLUMN photo_id int REFERENCES photo(id);
ALTER TABLE candidate DROP COLUMN photo_id;
ALTER TABLE candidate ADD COLUMN photo_id int REFERENCES photo(id) ON DELETE CASCADE;

CREATE TABLE users (
                           id SERIAL PRIMARY KEY,
                           name TEXT,
                           email TEXT,
                           password TEXT
);

ALTER TABLE users ADD UNIQUE (email);

CREATE TABLE city (
                       id SERIAL PRIMARY KEY,
                       name TEXT
);
ALTER TABLE candidate ADD COLUMN city_id int REFERENCES city(id);


DELETE FROM city where id > 50;
DELETE FROM candidate where id > 30;
INSERT INTO city (name) VALUES ('Стерлитамак');
INSERT INTO city (name) VALUES ('Санкт-Петербург');
INSERT INTO city (name) VALUES ('Москва');
INSERT INTO city (name) VALUES ('Уфа');
UPDATE candidate SET name = 'Вадим', city_id = 50  WHERE id = 55;

