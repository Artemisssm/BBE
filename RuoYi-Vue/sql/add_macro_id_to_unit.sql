-- Active: 1764770855277@@127.0.0.1@3306@bbe
-- ========================================
-- 为基带单元表添加宏配置ID字段
-- 创建日期：2025-12-16
-- ========================================

-- 添加宏配置ID字段
ALTER TABLE sys_baseband_unit 
ADD COLUMN macro_id BIGINT DEFAULT NULL COMMENT '宏配置ID' AFTER mode_type;

-- 添加索引
ALTER TABLE sys_baseband_unit 
ADD INDEX idx_macro_id (macro_id);

-- 显示结果
SELECT '========================================' AS '';
SELECT '字段添加完成！' AS '状态';
SELECT '========================================' AS '';

DESC sys_baseband_unit;
