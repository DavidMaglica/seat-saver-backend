-- This file is used to populate the database with some initial data

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
    role_id              INT         NOT NULL
);

INSERT INTO Users (id, username, email, password, role_id)
VALUES (1, 'david', 'david@mail.com', 'password', 1);

-- Notification options table
CREATE TABLE IF NOT EXISTS notification_options
(
    id                            INT AUTO_INCREMENT PRIMARY KEY,
    user_id                       INT     NOT NULL UNIQUE,
    push_notifications_turned_on  BOOLEAN NOT NULL,
    email_notifications_turned_on BOOLEAN NOT NULL,
    location_services_turned_on   BOOLEAN NOT NULL,
    CONSTRAINT fk_user_notification FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
);

INSERT INTO notification_options (id, user_id, push_notifications_turned_on, email_notifications_turned_on,
                                  location_services_turned_on)
VALUES (1, 1, true, true, false);