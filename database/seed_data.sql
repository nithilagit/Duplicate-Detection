-- ===================================================================
-- AI-Based Duplicate Question Detection System
-- Sample Seed Data Script (MySQL 8.0+)
-- ===================================================================

USE question_detection_db;

-- 1. Demo Faculty (Password: Admin@123 using BCrypt hash)
INSERT INTO faculty (faculty_id, name, email, password_hash, department, role, enabled)
VALUES (
    'FAC-AIDS-101',
    'Dr. Sarah Jenkins',
    'faculty@eec.srmrmp.edu.in',
    '$2a$10$wE8wY0uE6rXv83xH5kC1beH6e7r/jCg8pGjP1yN3e7O7uI.Wb9wSy',
    'Artificial Intelligence and Data Science',
    'ROLE_FACULTY',
    TRUE
) ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 2. Core Sample Questions (Demonstrating Intentional Semantic Duplicates)
-- Computer Networks - Duplicate Pair 1 (TCP Congestion)
INSERT INTO questions (id, question_text, subject, unit, course_outcome, bloom_level, marks, difficulty, question_type, usage_count, created_by_id)
VALUES (
    1,
    'Explain the working principle of TCP congestion control.',
    'Computer Networks', 'Unit III', 'CO3', 'Understand', 10, 'Medium', 'Descriptive', 2, 1
),
(
    2,
    'Describe how congestion control is handled in TCP.',
    'Computer Networks', 'Unit III', 'CO3', 'Understand', 10, 'Medium', 'Descriptive', 0, 1
);

-- Computer Networks - Duplicate Pair 2 (Dijkstra)
INSERT INTO questions (id, question_text, subject, unit, course_outcome, bloom_level, marks, difficulty, question_type, usage_count, created_by_id)
VALUES (
    3,
    'Explain Dijkstra\'s shortest path routing algorithm with an example.',
    'Computer Networks', 'Unit IV', 'CO4', 'Apply', 13, 'Hard', 'Problem', 1, 1
),
(
    4,
    'Describe the working mechanism of Dijkstra algorithm for finding shortest paths in computer networks.',
    'Computer Networks', 'Unit IV', 'CO4', 'Apply', 13, 'Hard', 'Problem', 0, 1
);

-- Discrete Mathematics - Duplicate Pair 2
INSERT INTO questions (id, question_text, subject, unit, course_outcome, bloom_level, marks, difficulty, question_type, usage_count, created_by_id)
VALUES (
    5,
    'Define an equivalence relation and determine whether a relation is reflexive, symmetric, and transitive.',
    'Discrete Mathematics', 'Unit I', 'CO1', 'Understand', 8, 'Medium', 'Descriptive', 1, 1
),
(
    6,
    'Explain the conditions for an equivalence relation with examples of reflexivity, symmetry, and transitivity.',
    'Discrete Mathematics', 'Unit I', 'CO1', 'Understand', 8, 'Medium', 'Descriptive', 0, 1
);

-- Machine Learning Techniques - Duplicate Pair 3
INSERT INTO questions (id, question_text, subject, unit, course_outcome, bloom_level, marks, difficulty, question_type, usage_count, created_by_id)
VALUES (
    7,
    'Explain the difference between supervised and unsupervised learning algorithms with real-world examples.',
    'Machine Learning Techniques', 'Unit I', 'CO1', 'Understand', 8, 'Easy', 'Descriptive', 1, 1
),
(
    8,
    'Differentiate between supervised learning and unsupervised machine learning methods with appropriate use cases.',
    'Machine Learning Techniques', 'Unit I', 'CO1', 'Understand', 8, 'Easy', 'Descriptive', 0, 1
);

-- Additional Diverse Questions across New Subjects
INSERT INTO questions (id, question_text, subject, unit, course_outcome, bloom_level, marks, difficulty, question_type, usage_count, created_by_id)
VALUES (
    9,
    'Compare the OSI reference model with the TCP/IP protocol suite across all layers.',
    'Computer Networks', 'Unit I', 'CO1', 'Analyze', 8, 'Medium', 'Descriptive', 1, 1
),
(
    10,
    'Explain the insertion and balancing operations in an AVL tree with rotation examples.',
    'Advanced Data Structures and Algorithms', 'Unit II', 'CO2', 'Apply', 10, 'Hard', 'Descriptive', 1, 1
),
(
    11,
    'Compare CISC and RISC architectures with respect to embedded microcontroller design and execution speed.',
    'Embedded System Design', 'Unit I', 'CO1', 'Analyze', 8, 'Medium', 'Descriptive', 1, 1
),
(
    12,
    'Explain runtime polymorphism and dynamic method dispatch in Java with code examples.',
    'Object Oriented Programming using Java', 'Unit II', 'CO2', 'Apply', 8, 'Medium', 'Descriptive', 1, 1
);

-- 3. Sample Similarity Audit Logs
INSERT INTO similarity_checks (new_question_text, matched_question_id, matched_question_text, similarity_score, status, recommendation, faculty_id)
VALUES (
    'Describe how congestion control is handled in TCP.',
    1,
    'Explain the working principle of TCP congestion control.',
    91.4,
    'HIGHLY_SIMILAR',
    'High semantic similarity detected (91.4%). Both questions discuss TCP congestion control mechanisms.',
    1
),
(
    'Differentiate between supervised learning and unsupervised machine learning methods with appropriate use cases.',
    7,
    'Explain the difference between supervised and unsupervised learning algorithms with real-world examples.',
    86.8,
    'HIGHLY_SIMILAR',
    'High semantic similarity detected (86.8%). Both questions discuss supervised vs unsupervised learning.',
    1
),
(
    'Explain quantum computing qubit superposition and entanglement gates.',
    NULL,
    NULL,
    14.5,
    'UNIQUE',
    'The question appears to be unique. No significant semantic overlap detected.',
    1
);

