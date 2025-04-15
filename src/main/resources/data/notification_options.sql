CREATE TABLE IF NOT EXISTS notification_options
(
    id                          INT AUTO_INCREMENT PRIMARY KEY,
    user_id                     INT     NOT NULL UNIQUE,
    push_notifications_enabled  BOOLEAN NOT NULL,
    email_notifications_enabled BOOLEAN NOT NULL,
    location_services_enabled   BOOLEAN NOT NULL,
    CONSTRAINT fk_user_notification FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);