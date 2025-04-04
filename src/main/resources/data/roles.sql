Create Table IF NOT EXISTS roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL
    );

INSERT INTO roles (id, role)
VALUES (1, 'USER'),
       (2, 'OWNER');