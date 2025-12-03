-- ========================================
-- 为参数定义表添加排序字段
-- ========================================

-- 添加 sort_order 字段
ALTER TABLE sys_baseband_param_def 
ADD COLUMN sort_order INT DEFAULT 999 COMMENT '显示排序，数字越小越靠前' 
AFTER bit_width_type;

-- 为现有参数设置排序值（使用param_id作为初始排序）
UPDATE sys_baseband_param_def 
SET sort_order = param_id;

-- 验证
SELECT '排序字段添加完成' AS result;
DESC sys_baseband_param_def;
