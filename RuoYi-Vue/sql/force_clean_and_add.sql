-- ========================================
-- 强制清理并添加16进制参数
-- 一次性完成所有操作
-- ========================================

-- 强制删除所有可能存在的HEX参数（逐个删除）
DELETE FROM sys_baseband_param_def WHERE param_code = 'ENC_FRAME_HEADER';
DELETE FROM sys_baseband_param_def WHERE param_code = 'ENC_START_ADDR';
DELETE FROM sys_baseband_param_def WHERE param_code = 'ENC_SYNC_WORD_HEX';
DELETE FROM sys_baseband_param_def WHERE param_code = 'ENC_SCRAMBLER_SEED';
DELETE FROM sys_baseband_param_def WHERE param_code = 'ENC_CRC_POLY';
DELETE FROM sys_baseband_param_def WHERE param_code = 'ENC_MASK_PATTERN';

DELETE FROM sys_baseband_param_def WHERE param_code = 'MOD_FRAME_HEADER';
DELETE FROM sys_baseband_param_def WHERE param_code = 'MOD_START_ADDR';
DELETE FROM sys_baseband_param_def WHERE param_code = 'MOD_PILOT_PATTERN';
DELETE FROM sys_baseband_param_def WHERE param_code = 'MOD_SCRAMBLER_INIT';
DELETE FROM sys_baseband_param_def WHERE param_code = 'MOD_SYNC_MARKER';
DELETE FROM sys_baseband_param_def WHERE param_code = 'MOD_FRAME_ID';

DELETE FROM sys_baseband_param_def WHERE param_code = 'DEMOD_FRAME_HEADER';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEMOD_START_ADDR';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEMOD_SYNC_WORD';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEMOD_DESCRAMBLER_SEED';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEMOD_PILOT_MASK';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEMOD_CARRIER_OFFSET';

DELETE FROM sys_baseband_param_def WHERE param_code = 'DEC_FRAME_HEADER';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEC_START_ADDR';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEC_SYNC_PATTERN';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEC_DESCRAMBLER_INIT';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEC_CRC_CHECK';
DELETE FROM sys_baseband_param_def WHERE param_code = 'DEC_ERROR_MASK';

-- 再次确认删除所有HEX类型
DELETE FROM sys_baseband_param_def WHERE value_type = 'HEX';

SELECT '清理完成，开始添加参数...' AS status;

-- ========================================
-- 按顺序插入参数
-- ========================================

-- 编码单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('ENCODE', 'ENC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW());

-- 调制单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('MODULATE', 'MOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW());

-- 解调单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('DEMODULATE', 'DEMOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW());

-- 译码单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('DECODE', 'DEC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW());

-- 编码单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_SYNC_WORD_HEX', '同步字', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '帧同步字', NOW()),
('ENCODE', 'ENC_SCRAMBLER_SEED', '扰码种子', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '扰码种子', NOW()),
('ENCODE', 'ENC_CRC_POLY', 'CRC多项式', 'HEX', 0, 4294967295, 1, 'U32', '04C11DB7', 'CRC32多项式', NOW()),
('ENCODE', 'ENC_MASK_PATTERN', '掩码模式', 'HEX', 0, 255, 1, 'U8', 'AA', '数据掩码', NOW());

-- 调制单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_PILOT_PATTERN', '导频图案', 'HEX', 0, 4294967295, 1, 'U32', '12345678', '导频模式', NOW()),
('MODULATE', 'MOD_SCRAMBLER_INIT', '扰码初值', 'HEX', 0, 65535, 1, 'U16', '0001', '扰码初值', NOW()),
('MODULATE', 'MOD_SYNC_MARKER', '同步标记', 'HEX', 0, 255, 1, 'U8', '47', 'TS同步字节', NOW()),
('MODULATE', 'MOD_FRAME_ID', '帧标识', 'HEX', 0, 65535, 1, 'U16', 'ABCD', '帧ID', NOW());

-- 解调单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_SYNC_WORD', '同步字', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '同步字', NOW()),
('DEMODULATE', 'DEMOD_DESCRAMBLER_SEED', '解扰种子', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '解扰种子', NOW()),
('DEMODULATE', 'DEMOD_PILOT_MASK', '导频掩码', 'HEX', 0, 4294967295, 1, 'U32', 'FFFFFFFF', '导频掩码', NOW()),
('DEMODULATE', 'DEMOD_CARRIER_OFFSET', '载波偏移', 'HEX', 0, 65535, 1, 'U16', '0000', '载波偏移', NOW());

-- 译码单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_SYNC_PATTERN', '同步模式', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '同步模式', NOW()),
('DECODE', 'DEC_DESCRAMBLER_INIT', '解扰初值', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '解扰初值', NOW()),
('DECODE', 'DEC_CRC_CHECK', 'CRC校验值', 'HEX', 0, 4294967295, 1, 'U32', '04C11DB7', 'CRC校验', NOW()),
('DECODE', 'DEC_ERROR_MASK', '错误掩码', 'HEX', 0, 255, 1, 'U8', '00', '错误掩码', NOW());

-- 验证
SELECT '========================================' AS '';
SELECT '参数添加完成！' AS result;
SELECT '========================================' AS '';

SELECT unit_type, COUNT(*) as total,
       SUM(CASE WHEN value_type = 'HEX' THEN 1 ELSE 0 END) as hex_count
FROM sys_baseband_param_def 
GROUP BY unit_type;

SELECT '========================================' AS '';
SELECT 'HEX参数列表（按param_id排序）：' AS '';
SELECT '========================================' AS '';

SELECT param_id, unit_type, param_code, param_name, bit_width_type, default_value
FROM sys_baseband_param_def
WHERE value_type = 'HEX'
ORDER BY unit_type, param_id;
