-- ============================================
-- TripSplitter - Database Schema
-- Smart Trip Expense Sharing System
-- ============================================
-- Database: trip_splitter (MySQL/XAMPP)
-- This file creates the complete database schema
-- for running the project locally with MySQL.
-- ============================================

-- Create the database
CREATE DATABASE IF NOT EXISTS trip_splitter;
USE trip_splitter;

-- ============================================
-- Table: users
-- Stores user account information
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- Table: trips
-- Stores trip information
-- ============================================
CREATE TABLE IF NOT EXISTS trips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT DEFAULT '',
    destination VARCHAR(200) DEFAULT '',
    start_date DATE,
    end_date DATE,
    invite_code VARCHAR(8) UNIQUE DEFAULT (UPPER(SUBSTRING(MD5(RAND()), 1, 8))),
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- Table: trip_members
-- Maps users to trips (many-to-many relationship)
-- ============================================
CREATE TABLE IF NOT EXISTS trip_members (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT NOT NULL,
    user_id INT NOT NULL,
    role VARCHAR(20) DEFAULT 'member',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY (trip_id, user_id)
);

-- ============================================
-- Table: expenses
-- Stores expense records for each trip
-- ============================================
CREATE TABLE IF NOT EXISTS expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT DEFAULT '',
    amount DECIMAL(10,2) NOT NULL,
    paid_by INT NOT NULL,
    expense_date DATE NOT NULL,
    category VARCHAR(50) DEFAULT 'general',
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (paid_by) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (amount > 0)
);

-- ============================================
-- Table: expense_splits
-- Stores individual share of each expense per member
-- ============================================
CREATE TABLE IF NOT EXISTS expense_splits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    expense_id INT NOT NULL,
    user_id INT NOT NULL,
    share_amount DECIMAL(10,2) NOT NULL,
    is_settled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- Table: settlements
-- Stores payment settlements between users
-- ============================================
CREATE TABLE IF NOT EXISTS settlements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT NOT NULL,
    payer_id INT NOT NULL,
    payee_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    is_settled BOOLEAN DEFAULT FALSE,
    settled_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE,
    FOREIGN KEY (payer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (payee_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (amount > 0)
);

-- ============================================
-- Indexes for performance
-- ============================================
CREATE INDEX idx_trips_created_by ON trips(created_by);
CREATE INDEX idx_trip_members_trip_id ON trip_members(trip_id);
CREATE INDEX idx_trip_members_user_id ON trip_members(user_id);
CREATE INDEX idx_expenses_trip_id ON expenses(trip_id);
CREATE INDEX idx_expenses_paid_by ON expenses(paid_by);
CREATE INDEX idx_expense_splits_expense_id ON expense_splits(expense_id);
CREATE INDEX idx_expense_splits_user_id ON expense_splits(user_id);
CREATE INDEX idx_settlements_trip_id ON settlements(trip_id);
CREATE INDEX idx_settlements_payer_id ON settlements(payer_id);
CREATE INDEX idx_settlements_payee_id ON settlements(payee_id);

-- ============================================
-- Sample Data (Optional - for testing)
-- ============================================

-- Insert sample users (passwords are SHA-256 hashed)
-- Password for all: "password123"
INSERT INTO users (name, email, password) VALUES
('Rahul Sharma', 'rahul@example.com', 'ef92b778ba5a4d2e8f6c1c4e7d8a9b0f1234567890abcdef1234567890abcdef'),
('Priya Patel', 'priya@example.com', 'ef92b778ba5a4d2e8f6c1c4e7d8a9b0f1234567890abcdef1234567890abcdef'),
('Amit Kumar', 'amit@example.com', 'ef92b778ba5a4d2e8f6c1c4e7d8a9b0f1234567890abcdef1234567890abcdef'),
('Sneha Gupta', 'sneha@example.com', 'ef92b778ba5a4d2e8f6c1c4e7d8a9b0f1234567890abcdef1234567890abcdef');

-- Insert sample trip
INSERT INTO trips (name, description, destination, start_date, end_date, created_by) VALUES
('Goa Trip 2024', 'Annual friends trip to Goa', 'Goa, India', '2024-12-20', '2024-12-25', 1);

-- Insert sample trip members
INSERT INTO trip_members (trip_id, user_id, role) VALUES
(1, 1, 'owner'),
(1, 2, 'member'),
(1, 3, 'member'),
(1, 4, 'member');

-- Insert sample expenses
INSERT INTO expenses (trip_id, title, amount, paid_by, expense_date, category, created_by) VALUES
(1, 'Hotel Booking', 8000.00, 1, '2024-12-20', 'hotel', 1),
(1, 'Petrol', 2000.00, 2, '2024-12-20', 'transport', 2),
(1, 'Dinner', 1500.00, 3, '2024-12-20', 'food', 3),
(1, 'Beach Tickets', 1000.00, 4, '2024-12-21', 'tickets', 4);

-- Insert sample expense splits (equal split: total=12500, per person=3125)
INSERT INTO expense_splits (expense_id, user_id, share_amount, is_settled) VALUES
(1, 1, 2000.00, FALSE),
(1, 2, 2000.00, FALSE),
(1, 3, 2000.00, FALSE),
(1, 4, 2000.00, FALSE),
(2, 1, 500.00, FALSE),
(2, 2, 500.00, FALSE),
(2, 3, 500.00, FALSE),
(2, 4, 500.00, FALSE),
(3, 1, 375.00, FALSE),
(3, 2, 375.00, FALSE),
(3, 3, 375.00, FALSE),
(3, 4, 375.00, FALSE),
(4, 1, 250.00, FALSE),
(4, 2, 250.00, FALSE),
(4, 3, 250.00, FALSE),
(4, 4, 250.00, FALSE);
