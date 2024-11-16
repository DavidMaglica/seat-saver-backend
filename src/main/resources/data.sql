-- This file is used to populate the database with some initial data

-- Users table
Create Table IF NOT EXISTS Users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email    VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL,
    last_known_latitude DOUBLE,
    last_known_longitude DOUBLE
);

-- Roles table
Create Table IF NOT EXISTS roles
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT         NOT NULL,
    role    VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_role FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- Notification options table
CREATE TABLE IF NOT EXISTS notification_options
(
    id                            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                       INT     NOT NULL UNIQUE,
    push_notifications_turned_on  BOOLEAN NOT NULL,
    email_notifications_turned_on BOOLEAN NOT NULL,
    location_services_turned_on   BOOLEAN NOT NULL,
    CONSTRAINT fk_user_notification FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);
