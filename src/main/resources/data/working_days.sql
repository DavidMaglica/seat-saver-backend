CREATE TABLE IF NOT EXISTS venue_working_days
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    venue_id    INT      NOT NULL,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 0 AND 6),
    CONSTRAINT fk_venue FOREIGN KEY (venue_id) REFERENCES venues (id),
    CONSTRAINT uq_venue_day UNIQUE (venue_id, day_of_week)
);