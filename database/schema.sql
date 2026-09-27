-- ===================================================================
-- AI-Based Duplicate Question Detection System
-- Relational Database Schema (MySQL 8.0+)
-- ===================================================================

CREATE DATABASE IF NOT EXISTS question_detection_db;
USE question_detection_db;

-- 1. Faculty Table
CREATE TABLE IF NOT EXISTS faculty (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_FACULTY',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_faculty_email (email),
    INDEX idx_faculty_code (faculty_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Questions Table
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_text TEXT NOT NULL,
    subject VARCHAR(100) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    course_outcome VARCHAR(50) NOT NULL,
    bloom_level VARCHAR(50) NOT NULL,
    marks INT NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    usage_count INT NOT NULL DEFAULT 0,
    created_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_questions_faculty FOREIGN KEY (created_by_id) REFERENCES faculty(id) ON DELETE SET NULL,
    INDEX idx_questions_subject (subject),
    INDEX idx_questions_unit (unit),
    INDEX idx_questions_co (course_outcome),
    INDEX idx_questions_bloom (bloom_level),
    INDEX idx_questions_diff (difficulty),
    INDEX idx_questions_type (question_type),
    FULLTEXT idx_question_text_search (question_text)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Question Embeddings Table
CREATE TABLE IF NOT EXISTS question_embeddings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL UNIQUE,
    embedding_json LONGTEXT NOT NULL,
    model_version VARCHAR(100) NOT NULL DEFAULT 'tfidf-ngram-cosine-v1',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_embedding_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Similarity Checks Table
CREATE TABLE IF NOT EXISTS similarity_checks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    new_question_text TEXT NOT NULL,
    matched_question_id BIGINT,
    matched_question_text TEXT,
    similarity_score DOUBLE NOT NULL,
    status VARCHAR(50) NOT NULL,
    recommendation TEXT,
    faculty_id BIGINT,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sim_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE SET NULL,
    CONSTRAINT fk_sim_matched_q FOREIGN KEY (matched_question_id) REFERENCES questions(id) ON DELETE SET NULL,
    INDEX idx_sim_status (status),
    INDEX idx_sim_checked_at (checked_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Question Papers Table
CREATE TABLE IF NOT EXISTS question_papers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    subject VARCHAR(100) NOT NULL,
    exam_code VARCHAR(50),
    academic_year VARCHAR(50) NOT NULL,
    semester VARCHAR(50) NOT NULL,
    total_marks INT NOT NULL,
    instructions TEXT,
    created_by_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_papers_faculty FOREIGN KEY (created_by_id) REFERENCES faculty(id) ON DELETE SET NULL,
    INDEX idx_papers_subject (subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Question Paper Questions Join Table
CREATE TABLE IF NOT EXISTS question_paper_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_paper_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    question_number INT NOT NULL,
    section_name VARCHAR(50) NOT NULL DEFAULT 'Part A',
    allocated_marks INT NOT NULL,
    CONSTRAINT fk_qpq_paper FOREIGN KEY (question_paper_id) REFERENCES question_papers(id) ON DELETE CASCADE,
    CONSTRAINT fk_qpq_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_qpq_paper (question_paper_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Question Usage Table
CREATE TABLE IF NOT EXISTS question_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    question_paper_id BIGINT NOT NULL,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_qu_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_qu_paper FOREIGN KEY (question_paper_id) REFERENCES question_papers(id) ON DELETE CASCADE,
    INDEX idx_qu_question (question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Activity Logs Table
CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    faculty_id BIGINT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE SET NULL,
    INDEX idx_log_action (action),
    INDEX idx_log_time (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
