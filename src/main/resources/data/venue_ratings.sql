CREATE TABLE IF NOT EXISTS venue_ratings
(
    id       INT AUTO_INCREMENT PRIMARY KEY,
    venue_id INT    NOT NULL,
    rating   DOUBLE NOT NULL,
    CONSTRAINT fk_venue_rating FOREIGN KEY (venue_id) REFERENCES venues (id) ON DELETE CASCADE
);