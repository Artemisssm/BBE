-- 更新参数约束表的字段注释
-- 将"单元名称"改为"链路模式"

ALTER TABLE sys_baseband_param_constraint 
MODIFY COLUMN source_unit_name VARCHAR(50) COMMENT '源链路模式（*表示通配）';

ALTER TABLE sys_baseband_param_constraint 
MODIFY COLUMN target_unit_name VARCHAR(50) COMMENT '目标链路模式（*表示通配）';

-- 查看更新后的表结构
DESC sys_baseband_param_constraint;

-- 显示完成信息
SELECT '字段注释已更新：单元名称 → 链路模式' AS '状态';
