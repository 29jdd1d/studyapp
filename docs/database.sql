-- 考研学习小程序数据库初始化脚本
-- Database Initialization Script for Postgraduate Study Mini Program

-- 创建数据库
CREATE DATABASE IF NOT EXISTS studyapp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE studyapp;

-- 注意：以下表结构由JPA自动创建，此文件仅供参考

-- 1. 用户表
-- CREATE TABLE sys_user (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     open_id VARCHAR(100) UNIQUE NOT NULL COMMENT '微信OpenID',
--     nick_name VARCHAR(50) COMMENT '昵称',
--     avatar_url VARCHAR(255) COMMENT '头像URL',
--     gender VARCHAR(10) COMMENT '性别',
--     phone VARCHAR(20) COMMENT '手机号',
--     email VARCHAR(100) COMMENT '邮箱',
--     target_university VARCHAR(100) COMMENT '目标院校',
--     target_major VARCHAR(100) COMMENT '目标专业',
--     exam_year VARCHAR(20) COMMENT '考研年份',
--     study_days INT DEFAULT 0 COMMENT '学习天数',
--     study_hours INT DEFAULT 0 COMMENT '学习小时数',
--     completed_questions INT DEFAULT 0 COMMENT '完成题目数',
--     correct_questions INT DEFAULT 0 COMMENT '正确题目数',
--     role VARCHAR(20) DEFAULT 'USER' COMMENT '角色',
--     enabled BOOLEAN DEFAULT TRUE COMMENT '启用状态',
--     create_time DATETIME NOT NULL COMMENT '创建时间',
--     update_time DATETIME NOT NULL COMMENT '更新时间',
--     INDEX idx_open_id (open_id)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 学习资源表
-- CREATE TABLE learning_resource (
--     id BIGINT AUTO_INCREMENT PRIMARY KEY,
--     title VARCHAR(200) NOT NULL COMMENT '资源标题',
--     description TEXT COMMENT '资源描述',
--     type VARCHAR(20) NOT NULL COMMENT '资源类型(VIDEO/DOCUMENT/QUESTION_BANK)',
--     subject VARCHAR(50) NOT NULL COMMENT '科目',
--     chapter VARCHAR(100) COMMENT '章节',
--     section VARCHAR(100) COMMENT '节',
--     file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
--     cover_url VARCHAR(500) COMMENT '封面URL',
--     duration INT COMMENT '视频时长(秒)',
--     file_size BIGINT COMMENT '文件大小(字节)',
--     view_count INT DEFAULT 0 COMMENT '浏览次数',
--     download_count INT DEFAULT 0 COMMENT '下载次数',
--     published BOOLEAN DEFAULT FALSE COMMENT '发布状态',
--     uploader_id BIGINT COMMENT '上传者ID',
--     create_time DATETIME NOT NULL COMMENT '创建时间',
--     update_time DATETIME NOT NULL COMMENT '更新时间',
--     INDEX idx_subject (subject),
--     INDEX idx_type (type),
--     INDEX idx_published (published)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资源表';

-- 插入测试数据（可选）
-- INSERT INTO sys_user (open_id, nick_name, role, enabled, create_time, update_time) 
-- VALUES ('admin_openid', '管理员', 'ADMIN', TRUE, NOW(), NOW());
