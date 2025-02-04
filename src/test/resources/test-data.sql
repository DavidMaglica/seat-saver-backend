-- This file is used to populate the database with test data

-- Users table
Create Table IF NOT EXISTS users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email    VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    lastKnownLatitude DOUBLE,
    lastKnownLongitude DOUBLE
);

INSERT INTO Users (id, username, email, password)
VALUES (1, 'user1', 'user1@mail.com', 'password'),
VALUES (2, 'user2', 'user2@mail.com', 'password'),
VALUES (3, 'user3', 'user3@mail.com', 'password'),
VALUES (4, 'user4', 'user4@mail.com', 'password');

-- Roles table
Create Table IF NOT EXISTS roles
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    role    VARCHAR(50) NOT NULL,
);

INSERT INTO roles (id, role)
VALUES (1, 'USER'),
VALUES (2, 'OWNER'),

-- Notification options table
CREATE TABLE IF NOT EXISTS notification_options
(
    id                            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                       INT     NOT NULL UNIQUE,
    push_notifications_turned_on  BOOLEAN NOT NULL,
    email_notifications_turned_on BOOLEAN NOT NULL,
    location_services_turned_on   BOOLEAN NOT NULL,
    CONSTRAINT fk_user_notification FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO notification_options (id, user_id, push_notifications_turned_on, email_notifications_turned_on,
                                  location_services_turned_on)
VALUES (1, 1, true, true, true),
VALUES (2, 2, true, false, false),
VALUES (3, 3, false, true, true),
VALUES (4, 4, false, false, false);
