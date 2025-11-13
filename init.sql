-- Initialize admin user for the study app
-- This script creates a default admin user
-- Username: admin
-- Password: admin123 (change this in production!)

USE studyapp;

-- Insert admin user if not exists
INSERT IGNORE INTO sys_user (
    username, 
    password, 
    nick_name, 
    role, 
    enabled, 
    study_days, 
    study_hours, 
    completed_questions, 
    correct_questions, 
    create_time, 
    update_time
)
VALUES (
    'admin',
    -- BCrypt hash for 'admin123'
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EZjzGGRRz6BqJp4rBXj7.2',
    '系统管理员',
    'ADMIN',
    1,
    0,
    0,
    0,
    0,
    NOW(),
    NOW()
);

-- Verify admin user was created
SELECT 'Admin user created successfully:' as message;
SELECT id, username, nick_name, role, enabled 
FROM sys_user 
WHERE username = 'admin';
