Create Table IF NOT EXISTS Roles
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(50) NOT NULL
);

INSERT INTO Roles (id, role)
VALUES (1, 'USER'),
       (2, 'OWNER');