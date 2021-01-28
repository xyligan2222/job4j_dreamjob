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




