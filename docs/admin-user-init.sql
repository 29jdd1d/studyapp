-- 创建管理员用户初始化脚本
-- Admin User Initialization Script
-- 
-- 使用方法：
-- 1. 在数据库中执行此脚本创建管理员账户
-- 2. 默认管理员账号：admin
-- 3. 默认密码：admin123 (BCrypt加密后的值)
--
-- 注意：生产环境请修改默认密码！

-- 插入管理员用户 (密码: admin123)
-- BCrypt加密后的密码值
INSERT INTO sys_user (
    open_id, 
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
) VALUES (
    NULL,
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHrcH3Muc0kZQwvqnJdELe',
    '系统管理员',
    'ADMIN',
    true,
    0,
    0,
    0,
    0,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE username = username;

-- 验证插入
SELECT id, username, nick_name, role, enabled FROM sys_user WHERE username = 'admin';
