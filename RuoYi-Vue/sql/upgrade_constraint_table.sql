-- 升级参数约束表，支持更复杂的约束规则
-- 执行前请备份数据！

-- 1. 添加新字段
ALTER TABLE sys_baseband_param_constraint
ADD COLUMN action_type VARCHAR(20) COMMENT '动作类型：RANGE/FIXED/DISABLE/ENUM_LIMIT/SYNC/AUTO_SET' AFTER constraint_value;

ALTER TABLE sys_baseband_param_constraint
ADD COLUMN action_value TEXT COMMENT '动作值JSON' AFTER action_type;

ALTER TABLE sys_baseband_param_constraint
ADD COLUMN ui_effect VARCHAR(20) COMMENT 'UI效果：READONLY/DISABLED/HIDDEN/HIGHLIGHT' AFTER action_value;

-- 2. 修改源参数字段，允许为NULL（支持仅链路约束）
ALTER TABLE sys_baseband_param_constraint
MODIFY COLUMN source_param_name VARCHAR(64) COMMENT '源参数名称（NULL表示仅链路约束）';

-- 3. 添加索引
CREATE INDEX idx_action_type ON sys_baseband_param_constraint(action_type);
CREATE INDEX idx_ui_effect ON sys_baseband_param_constraint(ui_effect);

-- 4. 查看表结构
DESC sys_baseband_param_constraint;

-- 5. 显示完成信息
SELECT '约束表升级完成！' AS '状态',
       '新增字段：action_type, action_value, ui_effect' AS '说明';

-- 6. 示例：插入新类型的约束规则

-- 示例1：值范围约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    action_type, action_value, ui_effect,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', 'ENCODE', 'ENC_CODE_TYPE',
    '返向中低速数传', 'ENCODE', 'ENC_CODE_RATE',
    'VALUE_RANGE', '==CONV', '0.5,0.8',
    'RANGE', '{"min": 0.5, "max": 0.8}', 'HIGHLIGHT',
    15, '卷积码的码率范围应为0.5~0.8', '0', '值范围约束示例'
);

-- 示例2：固定值约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    action_type, action_value, ui_effect,
    priority, error_message, status, remark
) VALUES (
    'ACM数传', 'ENCODE', 'ACM_ENABLE',
    'ACM数传', 'ENCODE', 'ENC_CODE_RATE',
    'FIXED_VALUE', '==1', '0.75',
    'FIXED', '{"value": 0.75}', 'READONLY',
    20, 'ACM启用时码率固定为0.75', '0', '固定值约束示例'
);

-- 示例3：禁用约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    action_type, action_value, ui_effect,
    priority, error_message, status, remark
) VALUES (
    'ACM数传', 'ENCODE', 'AUTO_MODE',
    'ACM数传', 'ENCODE', 'ENC_CODE_RATE',
    'ENABLE_DISABLE', '==1', '',
    'DISABLE', '{}', 'DISABLED',
    20, '自动模式时不能手动设置码率', '0', '禁用约束示例'
);

-- 示例4：枚举限制约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    action_type, action_value, ui_effect,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', 'ENCODE', 'ENC_CODE_TYPE',
    '返向中低速数传', 'ENCODE', 'ENC_CODE_RATE',
    'ENUM_LIMIT', '==CONV', '0.5,0.67,0.75',
    'ENUM_LIMIT', '{"allowedValues": ["0.5", "0.67", "0.75"]}', 'HIGHLIGHT',
    15, '卷积码只能选择这些码率', '0', '枚举限制约束示例'
);

-- 示例5：值同步约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    action_type, action_value, ui_effect,
    priority, error_message, status, remark
) VALUES (
    'ACM数传', 'ENCODE', 'ACM_ENABLE',
    'ACM数传', 'MODULATE', 'ACM_ENABLE',
    'VALUE_SYNC', '', '',
    'SYNC', '{"autoSync": true}', 'READONLY',
    20, '编码和调制的ACM状态必须一致', '0', '值同步约束示例'
);

-- 示例6：链路模式约束（源参数为NULL）
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    action_type, action_value, ui_effect,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', 'MODULATE', NULL,
    '返向中低速数传', 'MODULATE', 'MOD_SYMBOL_RATE',
    'VALUE_RANGE', '', '1,100',
    'RANGE', '{"min": 1, "max": 100}', 'HIGHLIGHT',
    10, '返向中低速数传的符号率范围为1~100', '0', '链路模式约束示例'
);

-- 7. 查看插入的示例数据
SELECT 
    constraint_id,
    source_unit_name,
    source_param_name,
    target_param_name,
    constraint_type,
    action_type,
    ui_effect,
    priority,
    remark
FROM sys_baseband_param_constraint
WHERE remark LIKE '%示例%'
ORDER BY priority DESC;
