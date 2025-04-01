-- This file is used to populate the database with test data

-- Roles table
Create Table IF NOT EXISTS roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

INSERT INTO roles (id, role)
VALUES (1, 'USER'),
       (2, 'OWNER');

-- Users table
Create Table IF NOT EXISTS Users
(
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    username             VARCHAR(50) NOT NULL,
    email                VARCHAR(50) NOT NULL,
    password             VARCHAR(50) NOT NULL,
    last_known_latitude  DOUBLE,
    last_known_longitude DOUBLE,
    role_id              INT         NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

INSERT INTO Users (id, username, email, password, role_id)
VALUES (1, 'user1', 'user1@mail.com', 'password', 1),
       (2, 'user2', 'user2@mail.com', 'password', 1),
       (3, 'user3', 'user3@mail.com', 'password', 1),
       (4, 'user4', 'user4@mail.com', 'password', 1);

-- Notification options table
CREATE TABLE IF NOT EXISTS notification_options
(
    id                            INT AUTO_INCREMENT PRIMARY KEY,
    user_id                       INT     NOT NULL UNIQUE,
    push_notifications_turned_on  BOOLEAN NOT NULL,
    email_notifications_turned_on BOOLEAN NOT NULL,
    location_services_turned_on   BOOLEAN NOT NULL,
    CONSTRAINT fk_user_notification FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO notification_options (id, user_id, push_notifications_turned_on, email_notifications_turned_on,
                                  location_services_turned_on)
VALUES (1, 1, true, true, true),
       (2, 2, true, false, false),
       (3, 3, false, true, true),
       (4, 4, false, false, false);

-- Venues types table
CREATE TABLE IF NOT EXISTS venue_types
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL
);

INSERT INTO venue_types (id, type)
VALUES (1, 'Restaurant'),
       (2, 'Cafe'),
       (3, 'Bar'),
       (4, 'Club');
-- Venues types table end

-- Venues table
CREATE TABLE IF NOT EXISTS venues
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    owner_id      INT          NOT NULL,
    name          VARCHAR(50)  NOT NULL,
    location      VARCHAR(255) NOT NULL,
    workingHours  VARCHAR(20)  NOT NULL,
    averageRating DOUBLE       NOT NULL,
    venueTypeId   INT          NOT NULL,
    CONSTRAINT venue_type_fk FOREIGN KEY (venueTypeId) REFERENCES venue_types (id)
);

INSERT INTO Venues (id, owner_id, name, location, workingHours, averageRating, venueTypeId)
VALUES (1, 1, 'Venue 1', 'Location 1', '9:00-17:00', 0.0, 1),
       (2, 2, 'Venue 2', 'Location 2', '10:00-18:00', 0.0, 2),
       (3, 3, 'Venue 3', 'Location 3', '11:00-19:00', 0.0, 3),
       (4, 4, 'Venue 4', 'Location 4', '12:00-20:00', 0.0, 4);

-- Venues ratings table
CREATE TABLE IF NOT EXISTS venue_ratings
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    venue_id INT NOT NULL,
    rating   DOUBLE,
    CONSTRAINT fk_venue_rating FOREIGN KEY (venue_id) REFERENCES venues (id) ON DELETE CASCADE
);
INSERT INTO venue_ratings (id, venue_id, rating)
VALUES (1, 1, 4.5),
       (2, 2, 4.0),
       (3, 2, 3.0);
-- Venues ratings table end
