-- 为参数约束表添加模式类型字段
-- 执行日期: 2024-12-02

-- 添加模式类型字段（支持多个模式类型，用逗号分隔）
ALTER TABLE sys_baseband_param_constraint 
ADD COLUMN mode_types VARCHAR(200) DEFAULT NULL COMMENT '适用模式类型（多个用逗号分隔，如：KSA,KMA，空表示适用所有模式）' 
AFTER target_param_name;

-- 更新说明
-- mode_types 字段说明：
-- 1. 空值或NULL：表示该约束适用于所有模式类型
-- 2. 单个值：如 'KSA'，表示仅适用于KSA模式
-- 3. 多个值：如 'KSA,KMA'，表示适用于KSA和KMA模式
-- 4. 常见模式类型：KSA、KMA、SSA、基带数传

SELECT '模式类型字段添加完成' AS result;
