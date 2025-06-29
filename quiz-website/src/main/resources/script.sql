DROP DATABASE IF EXISTS db21;
CREATE DATABASE db21;
USE db21;
CREATE TABLE users (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        hashPassword VARCHAR(255) NOT NULL,
                        salt VARCHAR(255) NOT NULL,
                        firstName VARCHAR(100) NOT NULL,
                        lastName VARCHAR(100) NOT NULL,
                        userName VARCHAR(100) UNIQUE NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        imageURL VARCHAR(2083),
                        bio TEXT
);
CREATE TABLE friendships (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        friendSender_id INT NOT NULL,
                        friendReceiver_id INT  NOT NULL,
                        FOREIGN KEY (friendSender_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (friendReceiver_id) REFERENCES users(id) ON DELETE CASCADE,
                        UNIQUE (friendSender_id, friendReceiver_id)
);
CREATE TABLE friendship_requests (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        requestSender_id INT  NOT NULL,
                        requestReceiver_id INT  NOT NULL,
                        timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (requestSender_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (requestReceiver_id) REFERENCES users(id) ON DELETE CASCADE,
                        UNIQUE (requestSender_id, requestReceiver_id)
);
