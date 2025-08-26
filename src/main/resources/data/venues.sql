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