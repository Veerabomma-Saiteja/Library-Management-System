CREATE DATABASE IF NOT EXISTS library_db;

USE library_db;

CREATE TABLE IF NOT EXISTS books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'Available'
);

INSERT INTO books (title, author, category, status)
VALUES
('Java Programming', 'James Gosling', 'Programming', 'Available'),
('Database Systems', 'Raghu Ramakrishnan', 'Database', 'Available'),
('Web Development', 'Jon Duckett', 'Web', 'Available');