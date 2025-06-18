CREATE DATABASE db21;
USE db21;
CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       hashPassword VARCHAR(255) NOT NULL,
                       firstName VARCHAR(100) NOT NULL,
                       lastName VARCHAR(100) NOT NULL,
                       userName VARCHAR(100) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       imageURL VARCHAR(2083),
                       bio TEXT
);