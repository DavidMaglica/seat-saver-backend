CREATE TABLE IF NOT EXISTS Notification_options
(
    id                          INT AUTO_INCREMENT PRIMARY KEY,
    user_id                     INT     NOT NULL UNIQUE,
    push_notifications_enabled  BOOLEAN NOT NULL,
    email_notifications_enabled BOOLEAN NOT NULL,
    location_services_enabled   BOOLEAN NOT NULL,
    CONSTRAINT fk_user_notification FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE
);

INSERT INTO Notification_options (id, user_id, push_notifications_enabled, email_notifications_enabled,
                                  location_services_enabled)
VALUES (1, 1, true, true, false);