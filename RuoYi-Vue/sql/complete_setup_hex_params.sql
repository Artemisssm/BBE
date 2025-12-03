-- ========================================
-- 完整设置：添加排序字段并插入HEX参数
-- ========================================

-- 步骤1: 添加排序字段（如果不存在）
SET @col_exists = 0;
SELECT COUNT(*) INTO @col_exists 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'sys_baseband_param_def' 
  AND COLUMN_NAME = 'sort_order';

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE sys_baseband_param_def ADD COLUMN sort_order INT DEFAULT 999 COMMENT ''显示排序'' AFTER bit_width_type',
    'SELECT ''sort_order字段已存在'' AS info');
    
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 步骤2: 为现有参数设置排序值
UPDATE sys_baseband_param_def 
SET sort_order = param_id 
WHERE sort_order IS NULL OR sort_order = 999;

-- 步骤3: 删除已存在的HEX参数
DELETE FROM sys_baseband_param_def WHERE param_code IN (
    'ENC_FRAME_HEADER', 'ENC_START_ADDR', 'ENC_SYNC_WORD_HEX', 'ENC_SCRAMBLER_SEED', 'ENC_CRC_POLY', 'ENC_MASK_PATTERN',
    'MOD_FRAME_HEADER', 'MOD_START_ADDR', 'MOD_PILOT_PATTERN', 'MOD_SCRAMBLER_INIT', 'MOD_SYNC_MARKER', 'MOD_FRAME_ID',
    'DEMOD_FRAME_HEADER', 'DEMOD_START_ADDR', 'DEMOD_SYNC_WORD', 'DEMOD_DESCRAMBLER_SEED', 'DEMOD_PILOT_MASK', 'DEMOD_CARRIER_OFFSET',
    'DEC_FRAME_HEADER', 'DEC_START_ADDR', 'DEC_SYNC_PATTERN', 'DEC_DESCRAMBLER_INIT', 'DEC_CRC_CHECK', 'DEC_ERROR_MASK'
);

-- 步骤4: 插入HEX参数（设置sort_order为1-6，确保在最前面）

-- 编码单元
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, sort_order, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', 1, '5555', '固定值0x5555', NOW()),
('ENCODE', 'ENC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', 2, '00000000', '起始地址', NOW()),
('ENCODE', 'ENC_SYNC_WORD_HEX', '同步字', 'HEX', 0, 4294967295, 1, 'U32', 3, '1ACFFC1D', '帧同步字', NOW()),
('ENCODE', 'ENC_SCRAMBLER_SEED', '扰码种子', 'HEX', 0, 65535, 1, 'U16', 4, 'FFFF', '扰码种子', NOW()),
('ENCODE', 'ENC_CRC_POLY', 'CRC多项式', 'HEX', 0, 4294967295, 1, 'U32', 5, '04C11DB7', 'CRC32多项式', NOW()),
('ENCODE', 'ENC_MASK_PATTERN', '掩码模式', 'HEX', 0, 255, 1, 'U8', 6, 'AA', '数据掩码', NOW());

-- 调制单元
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, sort_order, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', 1, '5555', '固定值0x5555', NOW()),
('MODULATE', 'MOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', 2, '00000000', '起始地址', NOW()),
('MODULATE', 'MOD_PILOT_PATTERN', '导频图案', 'HEX', 0, 4294967295, 1, 'U32', 3, '12345678', '导频模式', NOW()),
('MODULATE', 'MOD_SCRAMBLER_INIT', '扰码初值', 'HEX', 0, 65535, 1, 'U16', 4, '0001', '扰码初值', NOW()),
('MODULATE', 'MOD_SYNC_MARKER', '同步标记', 'HEX', 0, 255, 1, 'U8', 5, '47', 'TS同步字节', NOW()),
('MODULATE', 'MOD_FRAME_ID', '帧标识', 'HEX', 0, 65535, 1, 'U16', 6, 'ABCD', '帧ID', NOW());

-- 解调单元
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, sort_order, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', 1, '5555', '固定值0x5555', NOW()),
('DEMODULATE', 'DEMOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', 2, '00000000', '起始地址', NOW()),
('DEMODULATE', 'DEMOD_SYNC_WORD', '同步字', 'HEX', 0, 4294967295, 1, 'U32', 3, '1ACFFC1D', '同步字', NOW()),
('DEMODULATE', 'DEMOD_DESCRAMBLER_SEED', '解扰种子', 'HEX', 0, 65535, 1, 'U16', 4, 'FFFF', '解扰种子', NOW()),
('DEMODULATE', 'DEMOD_PILOT_MASK', '导频掩码', 'HEX', 0, 4294967295, 1, 'U32', 5, 'FFFFFFFF', '导频掩码', NOW()),
('DEMODULATE', 'DEMOD_CARRIER_OFFSET', '载波偏移', 'HEX', 0, 65535, 1, 'U16', 6, '0000', '载波偏移', NOW());

-- 译码单元
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, sort_order, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', 1, '5555', '固定值0x5555', NOW()),
('DECODE', 'DEC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', 2, '00000000', '起始地址', NOW()),
('DECODE', 'DEC_SYNC_PATTERN', '同步模式', 'HEX', 0, 4294967295, 1, 'U32', 3, '1ACFFC1D', '同步模式', NOW()),
('DECODE', 'DEC_DESCRAMBLER_INIT', '解扰初值', 'HEX', 0, 65535, 1, 'U16', 4, 'FFFF', '解扰初值', NOW()),
('DECODE', 'DEC_CRC_CHECK', 'CRC校验值', 'HEX', 0, 4294967295, 1, 'U32', 5, '04C11DB7', 'CRC校验', NOW()),
('DECODE', 'DEC_ERROR_MASK', '错误掩码', 'HEX', 0, 255, 1, 'U8', 6, '00', '错误掩码', NOW());

-- 验证
SELECT '========================================' AS '';
SELECT '完成！' AS result;
SELECT '========================================' AS '';

SELECT unit_type, COUNT(*) as total,
       SUM(CASE WHEN value_type = 'HEX' THEN 1 ELSE 0 END) as hex_count
FROM sys_baseband_param_def 
GROUP BY unit_type;

SELECT '========================================' AS '';
SELECT '编码单元参数（按sort_order排序）：' AS '';
SELECT param_id, param_code, param_name, value_type, sort_order
FROM sys_baseband_param_def
WHERE unit_type = 'ENCODE'
ORDER BY sort_order
LIMIT 10;
