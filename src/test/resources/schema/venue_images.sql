CREATE TABLE IF NOT EXISTS venue_images
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       TEXT,
    venue_id   INT  NOT NULL,
    image_data BLOB NOT NULL,
    FOREIGN KEY (venue_id) REFERENCES venues (id) ON DELETE CASCADE
);
