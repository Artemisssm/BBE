-- ========================================
-- 正确版本：添加16进制参数
-- 使用正确的字段名
-- ========================================

-- 为编码单元添加16进制参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('ENCODE', 'ENC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW()),
('ENCODE', 'ENC_SYNC_WORD_HEX', '同步字', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '帧同步字', NOW()),
('ENCODE', 'ENC_SCRAMBLER_SEED', '扰码种子', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '扰码种子', NOW());

-- 为调制单元添加16进制参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('MODULATE', 'MOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW()),
('MODULATE', 'MOD_PILOT_PATTERN', '导频图案', 'HEX', 0, 4294967295, 1, 'U32', '12345678', '导频模式', NOW()),
('MODULATE', 'MOD_SCRAMBLER_INIT', '扰码初值', 'HEX', 0, 65535, 1, 'U16', '0001', '扰码初值', NOW());

-- 为解调单元添加16进制参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('DEMODULATE', 'DEMOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW()),
('DEMODULATE', 'DEMOD_SYNC_WORD', '同步字', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '同步字', NOW()),
('DEMODULATE', 'DEMOD_DESCRAMBLER_SEED', '解扰种子', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '解扰种子', NOW());

-- 为译码单元添加16进制参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555', NOW()),
('DECODE', 'DEC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '起始地址', NOW()),
('DECODE', 'DEC_SYNC_PATTERN', '同步模式', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '同步模式', NOW()),
('DECODE', 'DEC_DESCRAMBLER_INIT', '解扰初值', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '解扰初值', NOW());

-- 验证
SELECT '参数添加完成' AS result;
SELECT unit_type, COUNT(*) as count FROM sys_baseband_param_def GROUP BY unit_type;

-- 查看新添加的HEX参数
SELECT unit_type, param_code, param_name, bit_width_type, default_value
FROM sys_baseband_param_def
WHERE value_type = 'HEX'
ORDER BY unit_type, param_code;
