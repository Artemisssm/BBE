-- ========================================
-- 清理所有HEX类型的参数
-- ========================================

-- 查看要删除的参数
SELECT '即将删除以下HEX参数：' AS info;
SELECT param_id, unit_type, param_code, param_name 
FROM sys_baseband_param_def 
WHERE value_type = 'HEX'
ORDER BY unit_type, param_code;

-- 删除所有HEX类型的参数
DELETE FROM sys_baseband_param_def WHERE value_type = 'HEX';

-- 验证删除结果
SELECT CONCAT('已删除 ', ROW_COUNT(), ' 个HEX参数') AS result;

-- 查看剩余参数数量
SELECT unit_type, COUNT(*) as count 
FROM sys_baseband_param_def 
GROUP BY unit_type
ORDER BY unit_type;
