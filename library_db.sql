-- ============================================
--  LIBRARY MANAGEMENT SYSTEM - DATABASE SCRIPT
--  Object-Oriented and Visual Programming
--  Semester 20252
-- ============================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- ============================================
-- TABLE 1: categories
-- ============================================
CREATE TABLE IF NOT EXISTS categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT
);

-- ============================================
-- TABLE 2: books
-- ============================================
CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) UNIQUE NOT NULL,
    category_id INT,
    publisher VARCHAR(150),
    year_published INT,
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
);

-- ============================================
-- TABLE 3: members
-- ============================================
CREATE TABLE IF NOT EXISTS members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    member_name VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    register_date DATE NOT NULL,
    status ENUM('Active', 'Inactive') DEFAULT 'Active'
);

-- ============================================
-- TABLE 4: issued_books
-- ============================================
CREATE TABLE IF NOT EXISTS issued_books (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    date_issued DATE NOT NULL,
    due_date DATE NOT NULL,
    date_returned DATE,
    status ENUM('Issued', 'Returned', 'Overdue') DEFAULT 'Issued',
    fine DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE
);

-- ============================================
-- SAMPLE DATA: categories
-- ============================================
INSERT INTO categories (category_name, description) VALUES
('Fiction', 'Novels, short stories, and other fictional works'),
('Non-Fiction', 'Biographies, history, science, and factual works'),
('Technology', 'Books about computers, programming, and IT'),
('Science', 'Physics, chemistry, biology, and other sciences'),
('Mathematics', 'Calculus, algebra, statistics, and related topics');

-- ============================================
-- SAMPLE DATA: books
-- ============================================
INSERT INTO books (title, author, isbn, category_id, publisher, year_published, total_copies, available_copies) VALUES
('Clean Code', 'Robert C. Martin', '978-0132350884', 3, 'Prentice Hall', 2008, 3, 3),
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0743273565', 1, 'Scribner', 1925, 2, 2),
('Introduction to Algorithms', 'Thomas H. Cormen', '978-0262033848', 3, 'MIT Press', 2009, 2, 2),
('A Brief History of Time', 'Stephen Hawking', '978-0553380163', 4, 'Bantam Books', 1988, 2, 2),
('Calculus Early Transcendentals', 'James Stewart', '978-1285741550', 5, 'Cengage', 2015, 3, 3),
('Design Patterns', 'Gang of Four', '978-0201633610', 3, 'Addison-Wesley', 1994, 2, 2),
('To Kill a Mockingbird', 'Harper Lee', '978-0061935466', 1, 'HarperCollins', 1960, 2, 2),
('Sapiens', 'Yuval Noah Harari', '978-0062316097', 2, 'Harper', 2011, 3, 3);

-- ============================================
-- SAMPLE DATA: members
-- ============================================
INSERT INTO members (member_name, email, phone, address, register_date, status) VALUES
('Budi Santoso', 'budi@email.com', '081234567890', 'Jl. Merdeka No.1, Jakarta', '2024-01-10', 'Active'),
('Siti Rahayu', 'siti@email.com', '082345678901', 'Jl. Pahlawan No.5, Bandung', '2024-02-15', 'Active'),
('Andi Wijaya', 'andi@email.com', '083456789012', 'Jl. Sudirman No.10, Surabaya', '2024-03-20', 'Active'),
('Dewi Lestari', 'dewi@email.com', '084567890123', 'Jl. Diponegoro No.3, Yogyakarta', '2024-04-05', 'Active'),
('Riko Pratama', 'riko@email.com', '085678901234', 'Jl. Gatot Subroto No.7, Medan', '2024-05-12', 'Active');
