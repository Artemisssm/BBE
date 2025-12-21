-- 修复下发日志表，添加 create_time 字段
-- 执行日期：2025-12-21

-- 添加 create_time 字段
ALTER TABLE sys_baseband_dispatch_log 
ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 验证
DESC sys_baseband_dispatch_log;
