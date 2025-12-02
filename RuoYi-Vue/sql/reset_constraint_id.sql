-- 重置约束表的自增ID
-- 注意：这会删除所有约束数据！

-- 清空约束表
TRUNCATE TABLE sys_baseband_param_constraint;

-- 重置自增ID（TRUNCATE已经自动重置了，这行可选）
ALTER TABLE sys_baseband_param_constraint AUTO_INCREMENT = 1;

SELECT '约束表已清空，ID已重置为1' AS result;
