-- 为参数约束表添加模式类型字段
-- 模式类型：KSA, KMA, SSA, 基带数传

-- 1. 添加源模式类型字段
ALTER TABLE sys_baseband_param_constraint
ADD COLUMN source_mode_type VARCHAR(20) COMMENT '源模式类型（*表示通配）' AFTER source_unit_name;

-- 2. 添加目标模式类型字段
ALTER TABLE sys_baseband_param_constraint
ADD COLUMN target_mode_type VARCHAR(20) COMMENT '目标模式类型（*表示通配）' AFTER target_unit_name;

-- 3. 添加索引
CREATE INDEX idx_source_mode ON sys_baseband_param_constraint(source_mode_type);
CREATE INDEX idx_target_mode ON sys_baseband_param_constraint(target_mode_type);

-- 4. 查看表结构
DESC sys_baseband_param_constraint;

-- 5. 显示完成信息
SELECT '模式类型字段添加完成！' AS '状态',
       '新增字段：source_mode_type, target_mode_type' AS '说明';

-- 6. 示例：插入带模式类型的约束

-- 示例1：KSA模式下的约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_mode_type, source_unit_type, source_param_name,
    target_unit_name, target_mode_type, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', 'KSA', 'ENCODE', 'ENC_CODE_TYPE',
    '返向中低速数传', 'KSA', 'ENCODE', 'ENC_CODE_RATE',
    'VALUE_RANGE', '==CONV', '0.5,0.8',
    15, 'KSA模式下卷积码的码率范围应为0.5~0.8', '0', 'KSA模式约束示例'
);

-- 示例2：KMA模式下的约束
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_mode_type, source_unit_type, source_param_name,
    target_unit_name, target_mode_type, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', 'KMA', 'ENCODE', 'ENC_CODE_TYPE',
    '返向中低速数传', 'KMA', 'ENCODE', 'ENC_CODE_RATE',
    'VALUE_RANGE', '==CONV', '0.6,0.9',
    15, 'KMA模式下卷积码的码率范围应为0.6~0.9', '0', 'KMA模式约束示例'
);

-- 示例3：通配模式类型（适用所有模式）
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_mode_type, source_unit_type, source_param_name,
    target_unit_name, target_mode_type, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', '*', 'MODULATE', 'MOD_TYPE',
    '返向中低速数传', '*', 'MODULATE', 'MOD_SYMBOL_RATE',
    'VALUE_RANGE', '==BPSK', '1,100',
    10, '所有模式下BPSK调制的符号率范围应为1~100', '0', '通配模式约束示例'
);

-- 7. 查看插入的示例数据
SELECT 
    constraint_id,
    source_unit_name,
    source_mode_type,
    source_unit_type,
    source_param_name,
    target_mode_type,
    constraint_type,
    priority,
    remark
FROM sys_baseband_param_constraint
WHERE remark LIKE '%模式%'
ORDER BY priority DESC;

-- 使用说明：
-- 1. source_mode_type 和 target_mode_type 可以为：
--    - KSA
--    - KMA
--    - SSA
--    - 基带数传
--    - * (通配符，表示适用所有模式)
--    - NULL (不限制模式)
-- 
-- 2. 优先级匹配顺序：
--    精确匹配（链路+模式+单元类型）> 模式通配 > 链路通配 > 全通配
--
-- 3. 示例场景：
--    - KSA模式下的参数约束与KMA模式不同
--    - 某些约束适用于所有模式（使用*）
--    - 某些约束不区分模式（使用NULL）
