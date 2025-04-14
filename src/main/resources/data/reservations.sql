CREATE TABLE IF NOT EXISTS Reservations
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    user_id          INT      NOT NULL,
    venue_id         INT      NOT NULL,
    datetime         LONG     NOT NULL,
    number_of_guests SMALLINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES Venues (id) ON DELETE CASCADE,
    CONSTRAINT uc_user_venue_date UNIQUE (user_id, venue_id, datetime)
);