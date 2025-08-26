CREATE TABLE IF NOT EXISTS venues
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    owner_id           INT          NOT NULL,
    name               VARCHAR(50)  NOT NULL,
    location           VARCHAR(255) NOT NULL,
    working_hours      VARCHAR(20)  NOT NULL,
    maximum_capacity   INT NOT NULL CHECK (maximum_capacity >= 0),
    available_capacity INT NOT NULL CHECK (available_capacity >= 0 AND available_capacity <= maximum_capacity),
    average_rating     DOUBLE       NOT NULL,
    venue_type_id      INT          NOT NULL,
    description        VARCHAR(500) DEFAULT NULL,
    CONSTRAINT venue_type_fk FOREIGN KEY (venue_type_id) REFERENCES venue_types (id)
);

INSERT INTO venues (owner_id, name, location, working_hours, maximum_capacity, available_capacity, average_rating,
                    venue_type_id, description)
VALUES (1, 'Grand Hall', '123 Main St, Poreč', '09:00-22:00', 500, 500, 4.5, 1, 'A spacious venue for large events.');