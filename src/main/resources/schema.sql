CREATE DATABASE IF NOT EXISTS quizbot;

-- Create Users table
CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    telegram VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(255) NOT NULL,
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
    correctness BOOLEAN NOT NULL DEFAULT 1
);

-- Create AnswerHistory table
CREATE TABLE IF NOT EXISTS AnswerHistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    answerer INT NOT NULL,
    question INT NOT NULL,
    earned INT NOT NULL DEFAULT 0,
    FOREIGN KEY (answerer) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (question) REFERENCES Questions(id) ON DELETE CASCADE
);