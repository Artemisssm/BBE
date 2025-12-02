-- 创建参数约束规则表（增加单元名称维度）
DROP TABLE IF EXISTS sys_baseband_param_constraint;

CREATE TABLE sys_baseband_param_constraint (
    constraint_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '约束规则主键',
    source_unit_name VARCHAR(50) COMMENT '源单元名称（*表示通配）',
    source_unit_type VARCHAR(16) COMMENT '源单元类型（*表示通配）',
    source_param_name VARCHAR(64) NOT NULL COMMENT '源参数名称',
    target_unit_name VARCHAR(50) COMMENT '目标单元名称（*表示通配）',
    target_unit_type VARCHAR(16) COMMENT '目标单元类型（*表示通配）',
    target_param_name VARCHAR(64) NOT NULL COMMENT '目标参数名称',
    constraint_type VARCHAR(20) NOT NULL COMMENT '约束类型：VALUE_RANGE/ENABLE_DISABLE/VALUE_SYNC/VALUE_CALCULATE',
    constraint_condition VARCHAR(100) COMMENT '约束条件（如：==1, >=10）',
    constraint_value VARCHAR(200) COMMENT '约束值（如：0.5,0.8）',
    priority INT DEFAULT 10 COMMENT '优先级：数字越大优先级越高',
    error_message VARCHAR(500) COMMENT '错误提示信息',
    status CHAR(1) DEFAULT '0' COMMENT '状态：0=正常，1=停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (constraint_id),
    KEY idx_source_unit (source_unit_name, source_unit_type),
    KEY idx_target_unit (target_unit_name, target_unit_type),
    KEY idx_source_param (source_param_name),
    KEY idx_target_param (target_param_name),
    KEY idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带参数约束规则表';

-- 插入示例约束规则（简化版，用于测试）
-- 注意：这里只插入几条基本的约束规则作为示例
-- 完整的约束规则请参考 insert_param_constraints_by_unit_name.sql

-- 示例1：通用约束 - 所有编码单元的码率范围
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    priority, error_message, status, remark
) VALUES (
    '*', 'ENCODE', 'ENC_CODE_TYPE',
    '*', 'ENCODE', 'ENC_CODE_RATE',
    'VALUE_RANGE', '==CONV', '0.33,0.75',
    10, '卷积码的码率范围应为0.33~0.75', '0',
    '通用约束：适用于所有编码单元'
);

-- 示例2：特定单元约束 - 返向中低速数传的编码方式
INSERT INTO sys_baseband_param_constraint (
    source_unit_name, source_unit_type, source_param_name,
    target_unit_name, target_unit_type, target_param_name,
    constraint_type, constraint_condition, constraint_value,
    priority, error_message, status, remark
) VALUES (
    '返向中低速数传', 'ENCODE', 'ENC_CODE_TYPE',
    '返向中低速数传', 'ENCODE', 'ENC_CODE_RATE',
    'VALUE_RANGE', '==CONV', '0.5,0.8',
    15, '返向中低速数传卷积码的码率范围应为0.5~0.8', '0',
    '特定单元约束：优先级高于通用约束'
);

-- 查询所有约束规则
SELECT 
    constraint_id,
    source_unit_name,
    source_unit_type,
    source_param_name,
    target_unit_name,
    target_unit_type,
    target_param_name,
    constraint_type,
    constraint_condition,
    constraint_value,
    priority,
    error_message,
    status
FROM sys_baseband_param_constraint
WHERE status = '0'
ORDER BY priority DESC, constraint_id;
