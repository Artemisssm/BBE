-- ============================================
-- 基带参数定义表优化脚本
-- 执行时间：2025-12-03
-- ============================================

-- 1. 添加字段注释
ALTER TABLE sys_baseband_param_def 
MODIFY COLUMN sort_order INT COMMENT '排序值（按单元类型分组，从1开始）';

-- 2. 添加复合索引（提升排序查询性能）
-- 检查索引是否存在
SELECT COUNT(*) INTO @index_exists 
FROM information_schema.statistics 
WHERE table_schema = DATABASE() 
  AND table_name = 'sys_baseband_param_def' 
  AND index_name = 'idx_unit_type_sort_order';

-- 如果索引不存在则创建
SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_unit_type_sort_order ON sys_baseband_param_def(unit_type, sort_order)',
    'SELECT "索引已存在，跳过创建"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 3. 为现有数据初始化排序值（如果需要）
-- 按单元类型分组，为每个参数分配排序值
UPDATE sys_baseband_param_def t1
JOIN (
    SELECT 
        param_id,
        ROW_NUMBER() OVER (PARTITION BY unit_type ORDER BY param_id) as new_sort_order
    FROM sys_baseband_param_def
    WHERE sort_order IS NULL
) t2 ON t1.param_id = t2.param_id
SET t1.sort_order = t2.new_sort_order
WHERE t1.sort_order IS NULL;

-- 4. 查看优化结果
SELECT 
    unit_type AS '单元类型',
    COUNT(*) AS '参数数量',
    COUNT(sort_order) AS '已设置排序',
    COUNT(*) - COUNT(sort_order) AS '未设置排序'
FROM sys_baseband_param_def
GROUP BY unit_type
ORDER BY unit_type;

-- 5. 验证索引创建
SHOW INDEX FROM sys_baseband_param_def WHERE Key_name = 'idx_unit_type_sort_order';

-- ============================================
-- 优化说明：
-- 1. 添加了字段注释，便于理解字段用途
-- 2. 创建了复合索引，提升按单元类型和排序值查询的性能
-- 3. 为现有数据初始化排序值，确保数据完整性
-- 4. 提供了验证查询，确认优化效果
-- ============================================
