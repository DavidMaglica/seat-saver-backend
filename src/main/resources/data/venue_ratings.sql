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