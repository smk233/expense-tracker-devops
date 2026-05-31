-- ==========================================
-- DATABASE INITIALIZATION SCRIPT
-- expense_tracker_db
-- ==========================================

CREATE DATABASE IF NOT EXISTS expense_tracker_db;
USE expense_tracker_db;

-- ==========================================
-- ROLES TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- ==========================================
-- USERS TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ==========================================
-- USER_ROLES JUNCTION TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- ==========================================
-- EXPENSES TABLE
-- ==========================================
CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    amount DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ==========================================
-- SEED DATA
-- ==========================================

-- Insert Roles
INSERT IGNORE INTO roles (name) VALUES ('ROLE_USER');
INSERT IGNORE INTO roles (name) VALUES ('ROLE_ADMIN');

-- Insert Admin User (password: admin123)
-- BCrypt hash of 'admin123'
INSERT IGNORE INTO users (first_name, last_name, email, password, enabled)
VALUES ('Admin', 'User', 'admin@expense.com',
        '$2a$12$oEDnFZTufVPTOR8DKRB1G.8TxdY705bWWNv4fq8d4jBVNW4mS9wNe', TRUE);

-- Insert Demo User (password: user123)
INSERT IGNORE INTO users (first_name, last_name, email, password, enabled)
VALUES ('John', 'Doe', 'john@expense.com',
        '$2a$12$84RmK9UzWProEGa9j1y2gO7H674n1.9N9SWk3ZluPUoloTP2C0GSC', TRUE);

-- Keep demo passwords correct even if users already existed before this script ran
UPDATE users
SET password = '$2a$12$oEDnFZTufVPTOR8DKRB1G.8TxdY705bWWNv4fq8d4jBVNW4mS9wNe'
WHERE email = 'admin@expense.com';

UPDATE users
SET password = '$2a$12$84RmK9UzWProEGa9j1y2gO7H674n1.9N9SWk3ZluPUoloTP2C0GSC'
WHERE email = 'john@expense.com';

-- Assign Roles
INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@expense.com' AND r.name = 'ROLE_ADMIN';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@expense.com' AND r.name = 'ROLE_USER';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'john@expense.com' AND r.name = 'ROLE_USER';

-- Sample Expenses for Demo User
INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Grocery Shopping', 'Weekly groceries from supermarket', 2500.00, 'FOOD', 'CASH',
       DATE_SUB(CURDATE(), INTERVAL 2 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Electricity Bill', 'Monthly electricity payment', 1800.00, 'UTILITIES', 'ONLINE_TRANSFER',
       DATE_SUB(CURDATE(), INTERVAL 5 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Netflix Subscription', 'Monthly streaming subscription', 649.00, 'ENTERTAINMENT', 'CREDIT_CARD',
       DATE_SUB(CURDATE(), INTERVAL 7 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Fuel', 'Petrol for bike', 500.00, 'TRANSPORT', 'CASH',
       DATE_SUB(CURDATE(), INTERVAL 1 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Doctor Visit', 'Routine checkup', 800.00, 'HEALTH', 'DEBIT_CARD',
       DATE_SUB(CURDATE(), INTERVAL 10 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Online Course', 'Java Spring Boot course on Udemy', 1299.00, 'EDUCATION', 'CREDIT_CARD',
       DATE_SUB(CURDATE(), INTERVAL 15 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Restaurant Dinner', 'Dinner with family', 1500.00, 'FOOD', 'DEBIT_CARD',
       DATE_SUB(CURDATE(), INTERVAL 3 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';

INSERT IGNORE INTO expenses (title, description, amount, category, payment_method, expense_date, user_id)
SELECT 'Internet Bill', 'Broadband monthly plan', 999.00, 'UTILITIES', 'ONLINE_TRANSFER',
       DATE_SUB(CURDATE(), INTERVAL 8 DAY), u.id FROM users u WHERE u.email = 'john@expense.com';
