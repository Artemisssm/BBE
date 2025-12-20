-- Active: 1764770855277@@127.0.0.1@3306@bbe
-- ========================================
-- 基带宏管理模块 SQL 脚本
-- 创建日期：2025-12-09
-- 说明：宏管理表，用于预设参数配置模板
-- ========================================

-- 1. 宏定义表
DROP TABLE IF EXISTS sys_baseband_macro;
CREATE TABLE sys_baseband_macro (
    macro_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '宏ID',
    macro_name     VARCHAR(64)  NOT NULL COMMENT '宏名称',
    macro_code     VARCHAR(64)  NOT NULL COMMENT '宏编码（唯一）',
    unit_type      VARCHAR(16)  NOT NULL COMMENT '适用单元类型 ENCODE/MODULATE/DEMODULATE/DECODE',
    mode_type      VARCHAR(32)  DEFAULT NULL COMMENT '适用模式类型 KSA/KMA/SSA/基带数传',
    description    VARCHAR(255) DEFAULT NULL COMMENT '宏描述',
    is_default     CHAR(1)      DEFAULT '0' COMMENT '是否默认宏 0否 1是',
    status         CHAR(1)      DEFAULT '0' COMMENT '状态 0正常 1停用',
    sort_order     INT          DEFAULT 0 COMMENT '排序',
    create_by      VARCHAR(64)  DEFAULT NULL,
    create_time    DATETIME     DEFAULT NULL,
    update_by      VARCHAR(64)  DEFAULT NULL,
    update_time    DATETIME     DEFAULT NULL,
    remark         VARCHAR(500) DEFAULT NULL,
    PRIMARY KEY (macro_id),
    UNIQUE KEY uk_macro_code (macro_code),
    KEY idx_unit_type (unit_type),
    KEY idx_mode_type (mode_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带宏定义表';

-- 2. 宏参数值表
DROP TABLE IF EXISTS sys_baseband_macro_param;
CREATE TABLE sys_baseband_macro_param (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    macro_id       BIGINT       NOT NULL COMMENT '宏ID',
    param_id       BIGINT       NOT NULL COMMENT '参数定义ID',
    param_value    VARCHAR(64)  NOT NULL COMMENT '参数值',
    create_time    DATETIME     DEFAULT NULL,
    update_time    DATETIME     DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_macro_param (macro_id, param_id),
    KEY idx_macro_id (macro_id),
    KEY idx_param_id (param_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宏参数值表';

-- 3. 插入示例宏数据
-- 编码单元默认宏
INSERT INTO sys_baseband_macro (macro_name, macro_code, unit_type, mode_type, description, is_default, status, sort_order, create_by, create_time)
VALUES 
('编码标准配置', 'ENCODE_STANDARD', 'ENCODE', 'KSA', '编码单元标准参数配置', '1', '0', 1, 'admin', NOW()),
('编码高速配置', 'ENCODE_HIGH_SPEED', 'ENCODE', 'KMA', '编码单元高速传输配置', '0', '0', 2, 'admin', NOW()),
('调制标准配置', 'MODULATE_STANDARD', 'MODULATE', 'KSA', '调制单元标准参数配置', '1', '0', 3, 'admin', NOW()),
('解调标准配置', 'DEMODULATE_STANDARD', 'DEMODULATE', 'KSA', '解调单元标准参数配置', '1', '0', 4, 'admin', NOW()),
('译码标准配置', 'DECODE_STANDARD', 'DECODE', 'KSA', '译码单元标准参数配置', '1', '0', 5, 'admin', NOW());

-- 4. 为示例宏配置参数值（以编码标准配置为例）
-- 获取编码标准配置的宏ID
SET @macro_id = (SELECT macro_id FROM sys_baseband_macro WHERE macro_code = 'ENCODE_STANDARD');

-- 为该宏配置一些常用参数的默认值
INSERT INTO sys_baseband_macro_param (macro_id, param_id, param_value, create_time)
SELECT @macro_id, param_id, default_value, NOW()
FROM sys_baseband_param_def
WHERE unit_type = 'ENCODE' AND default_value IS NOT NULL
LIMIT 10;

-- 5. 显示创建结果
SELECT '========================================' AS '';
SELECT '宏管理表创建完成！' AS '状态';
SELECT '========================================' AS '';

SELECT 
    macro_id AS '宏ID',
    macro_name AS '宏名称',
    macro_code AS '宏编码',
    unit_type AS '单元类型',
    mode_type AS '模式类型',
    is_default AS '默认',
    status AS '状态'
FROM sys_baseband_macro
ORDER BY sort_order;

SELECT '========================================' AS '';
SELECT '已创建5个示例宏配置' AS '说明';
SELECT '========================================' AS '';
