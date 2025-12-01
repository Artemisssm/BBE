-- 修改基带参数定义表字段
-- 1. 缩放倍数改为量化单位
-- 2. 位宽改为位宽类型（下拉框）
-- 3. 删除硬件顺序，添加步进设置

-- 备份原表（可选）
-- CREATE TABLE sys_baseband_param_def_backup AS SELECT * FROM sys_baseband_param_def;

-- 删除硬件顺序字段
ALTER TABLE sys_baseband_param_def DROP COLUMN hardware_order;

-- 修改缩放倍数字段为量化单位
ALTER TABLE sys_baseband_param_def CHANGE COLUMN scale_factor quantization_unit INT DEFAULT 1 COMMENT '量化单位（下发给硬件时需要除以它）';

-- 修改位宽字段为位宽类型
ALTER TABLE sys_baseband_param_def CHANGE COLUMN bit_length bit_width_type VARCHAR(10) DEFAULT 'U16' COMMENT '位宽类型：U8/U16/U32/I8/I16/I32';

-- 添加步进值字段
ALTER TABLE sys_baseband_param_def ADD COLUMN step_value DECIMAL(10,2) DEFAULT 1.00 COMMENT '步进值' AFTER bit_width_type;

-- 更新现有数据的位宽类型（根据原来的bit_length值推断）
-- 注意：这个更新语句需要根据实际情况调整
UPDATE sys_baseband_param_def SET bit_width_type = 'U8' WHERE bit_width_type = '8';
UPDATE sys_baseband_param_def SET bit_width_type = 'U16' WHERE bit_width_type = '16';
UPDATE sys_baseband_param_def SET bit_width_type = 'U32' WHERE bit_width_type = '32';
UPDATE sys_baseband_param_def SET bit_width_type = 'U16' WHERE bit_width_type IS NULL OR bit_width_type = '';

-- 验证修改结果
SELECT param_id, param_code, param_name, quantization_unit, bit_width_type, step_value 
FROM sys_baseband_param_def 
LIMIT 10;
