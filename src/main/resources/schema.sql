-- Create Users table
CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    telegram VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(255) NOT NULL
);

-- Create Questions table
CREATE TABLE IF NOT EXISTS Questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tag VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    creator INT NOT NULL,
    FOREIGN KEY (creator) REFERENCES Users(id) ON DELETE CASCADE
);

-- Create Options table
CREATE TABLE IF NOT EXISTS Options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    mark INT NOT NULL DEFAULT 1,
    correctness BOOLEAN NOT NULL DEFAULT 1,
    question INT NOT NULL,
    FOREIGN KEY (question) REFERENCES Questions(id) ON DELETE CASCADE
);

-- Create AnswerHistories table
CREATE TABLE IF NOT EXISTS AnswerHistories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    answerer INT NOT NULL,
    question INT NOT NULL,
    `option` INT NOT NULL,
    tag VARCHAR(255) NOT NULL,
    earned INT NOT NULL DEFAULT 0,
    FOREIGN KEY (`option`) REFERENCES Options(id) ON DELETE CASCADE,
    FOREIGN KEY (answerer) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (question) REFERENCES Questions(id) ON DELETE CASCADE
);