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
                        bio TEXT,
                        isAdmin BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE TABLE friendships (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        friendSenderId INT NOT NULL,
                        friendReceiverId INT  NOT NULL,
                        FOREIGN KEY (friendSenderId) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (friendReceiverId) REFERENCES users(id) ON DELETE CASCADE,
                        UNIQUE (friendSenderId, friendReceiverId)
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

CREATE TABLE messages (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          sender_id INT NOT NULL,
                          receiver_id INT NOT NULL,
                          content TEXT NOT NULL,
                          sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (sender_id) REFERENCES users(id),
                          FOREIGN KEY (receiver_id) REFERENCES users(id)
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
                                              points DOUBLE DEFAULT 10.0,
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

CREATE TABLE IF NOT EXISTS quiz_question_mapping (
                                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                     quiz_id BIGINT NOT NULL,
                                                     question_id BIGINT NOT NULL,
                                                     sequence_order BIGINT NOT NULL,
                                                     FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
                                                     FOREIGN KEY (question_id) REFERENCES test_questions(id) ON DELETE CASCADE,
                                                     UNIQUE (quiz_id, question_id),
                                                     UNIQUE (quiz_id, sequence_order)
);
CREATE TABLE quiz_ratings (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id INT NOT NULL,
                              quiz_id BIGINT NOT NULL,
                              rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
                              UNIQUE (user_id, quiz_id)
);

CREATE TABLE quiz_reviews (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id INT NOT NULL,
                              quiz_id BIGINT NOT NULL,
                              review_text TEXT NOT NULL,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
                              UNIQUE (user_id, quiz_id)
);
CREATE TABLE quiz_challenges (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 challenger_user_id INT NOT NULL,
                                 challenged_user_id INT NOT NULL,
                                 quiz_id BIGINT NOT NULL,
                                 message TEXT,
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 status ENUM('PENDING', 'ACCEPTED', 'COMPLETED', 'DECLINED') DEFAULT 'PENDING',
                                 FOREIGN KEY (challenger_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                 FOREIGN KEY (challenged_user_id) REFERENCES users(id) ON DELETE CASCADE,
                                 FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
                                 UNIQUE (challenger_user_id, challenged_user_id, quiz_id)
);
CREATE TABLE achievements (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              name VARCHAR(100) NOT NULL UNIQUE,
                              description TEXT,
                              icon_url VARCHAR(2083),
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE user_achievements (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   user_id INT NOT NULL,
                                   achievement_id BIGINT NOT NULL,
                                   awarded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                   FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
                                   UNIQUE (user_id, achievement_id)
);

CREATE TABLE announcements (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              title VARCHAR(255) NOT NULL,
                              content TEXT NOT NULL,
                              author_id INT NOT NULL,
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              is_active BOOLEAN DEFAULT TRUE,
                              FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);
INSERT INTO achievements (name, description, icon_url, created_at) VALUES
                                                                       ('AMATEUR_AUTHOR', 'Created at least 1 quiz', 'https://ih1.redbubble.net/image.779432435.1383/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg', NOW()),
                                                                       ('PROLIFIC_AUTHOR', 'Created at least 5 quizzes', 'https://ih1.redbubble.net/image.779432435.1383/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg', NOW()),
                                                                       ('PRODIGIOUS_AUTHOR', 'Created at least 10 quizzes', 'https://ih1.redbubble.net/image.779432435.1383/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg', NOW()),
                                                                       ('QUIZ_MACHINE', 'Taken at least 10 quizzes', 'https://ih1.redbubble.net/image.779432435.1383/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg', NOW()),
                                                                       ('QUIZ_MASTER', 'Mastered a quiz with high score', 'https://ih1.redbubble.net/image.779432435.1383/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg', NOW());
INSERT INTO quiz_categories (category_name, description, is_active) VALUES
                                                                        ('Science', 'Questions related to physics, chemistry, biology, and general science.', TRUE),
                                                                        ('History', 'Covers world history, events, and historical figures.', TRUE),
                                                                        ('Geography', 'Includes countries, capitals, landmarks, and maps.', TRUE),
                                                                        ('Mathematics', 'Covers arithmetic, algebra, geometry, and other branches.', TRUE),
                                                                        ('Literature', 'Focuses on authors, books, literary devices, and genres.', TRUE),
                                                                        ('Technology', 'Questions about modern tech, computing, and innovations.', TRUE),
                                                                        ('Sports', 'Covers rules, history, and facts about various sports.', TRUE),
                                                                        ('Movies', 'Film industry, directors, actors, and movie trivia.', TRUE),
                                                                        ('Music', 'Genres, artists, history of music, and instruments.', TRUE),
                                                                        ('Art', 'Painting, sculpture, art history, and famous artists.', TRUE);
