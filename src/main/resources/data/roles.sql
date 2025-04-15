Create Table IF NOT EXISTS roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

INSERT INTO Roles (id, role)
VALUES (0, 'USER'),
       (1, 'OWNER');