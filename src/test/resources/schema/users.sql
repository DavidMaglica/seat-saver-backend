Create Table IF NOT EXISTS users
(
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    username             VARCHAR(50)  NOT NULL,
    email                VARCHAR(50)  NOT NULL,
    password             VARCHAR(100) NOT NULL,
    last_known_latitude  DOUBLE,
    last_known_longitude DOUBLE,
    role_id              INT          NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT uc_email UNIQUE (email)
);