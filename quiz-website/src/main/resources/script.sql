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

CREATE TABLE IF NOT EXISTS quiz_categories (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               category_name VARCHAR(64) NOT NULL,
                                               description TEXT,
                                               is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS quizzes (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                       creator_user_id INT NOT NULL,
                                       category_id BIGINT,
                                       last_question_number BIGINT DEFAULT 0,
                                       created_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       test_title VARCHAR(128) NOT NULL,
                                       test_description VARCHAR(256),
                                       time_limit_minutes BIGINT DEFAULT 10,
                                       FOREIGN KEY (creator_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       FOREIGN KEY (category_id) REFERENCES quiz_categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS test_questions (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              author_user_id INT NOT NULL,
                                              category_id BIGINT DEFAULT NULL,
                                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                              question_data MEDIUMBLOB NOT NULL,
                                              question_title VARCHAR(128),
                                              question_type ENUM('TEXT', 'MULTIPLE_CHOICE', 'IMAGE') DEFAULT 'TEXT',
                                              FOREIGN KEY (author_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                              FOREIGN KEY (category_id) REFERENCES quiz_categories(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_sessions (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             participant_user_id INT UNIQUE NOT NULL,
                                             test_id BIGINT NOT NULL,
                                             current_question_num BIGINT DEFAULT 0,
                                             time_allocated BIGINT,
                                             session_start DATETIME DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (participant_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                             FOREIGN KEY (test_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS participant_answers (
                                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                   participant_user_id INT NOT NULL,
                                                   test_id BIGINT NOT NULL,
                                                   question_number BIGINT NOT NULL,
                                                   points_earned DOUBLE DEFAULT 0,
                                                   time_spent_seconds INT,
                                                   answer_text TEXT,
                                                   UNIQUE(participant_user_id, test_id, question_number),
                                                   FOREIGN KEY (participant_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                                   FOREIGN KEY (test_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_completions (
                                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                participant_user_id INT NOT NULL,
                                                test_id BIGINT NOT NULL,
                                                final_score DOUBLE DEFAULT 0,
                                                total_possible DOUBLE DEFAULT 0,
                                                completion_percentage DECIMAL(5,2),
                                                started_at DATETIME,
                                                finished_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                total_time_minutes INT,
                                                FOREIGN KEY (participant_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                                FOREIGN KEY (test_id) REFERENCES quizzes(id) ON DELETE CASCADE
);
