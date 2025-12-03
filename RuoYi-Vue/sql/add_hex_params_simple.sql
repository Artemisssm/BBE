-- ========================================
-- 简化版：添加帧头标志、起始地址和16进制参数
-- ========================================

-- 步骤1: 更新现有参数的hardware_order（为帧头和起始地址腾出位置）
UPDATE sys_baseband_param_def 
SET hardware_order = hardware_order + 2 
WHERE unit_type IN ('ENCODE', 'MODULATE', 'DEMODULATE', 'DECODE');

-- 步骤2: 为编码单元添加参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555', NOW()),
('ENCODE', 'ENC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '起始地址', NOW()),
('ENCODE', 'ENC_SYNC_WORD_HEX', '同步字', 'HEX', 0, 4294967295, 1, 32, 115, '1ACFFC1D', '帧同步字', NOW()),
('ENCODE', 'ENC_SCRAMBLER_SEED', '扰码种子', 'HEX', 0, 65535, 1, 16, 116, 'FFFF', '扰码种子', NOW());

-- 步骤3: 为调制单元添加参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555', NOW()),
('MODULATE', 'MOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '起始地址', NOW()),
('MODULATE', 'MOD_PILOT_PATTERN', '导频图案', 'HEX', 0, 4294967295, 1, 32, 115, '12345678', '导频模式', NOW()),
('MODULATE', 'MOD_SCRAMBLER_INIT', '扰码初值', 'HEX', 0, 65535, 1, 16, 116, '0001', '扰码初值', NOW());

-- 步骤4: 为解调单元添加参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555', NOW()),
('DEMODULATE', 'DEMOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '起始地址', NOW()),
('DEMODULATE', 'DEMOD_SYNC_WORD', '同步字', 'HEX', 0, 4294967295, 1, 32, 115, '1ACFFC1D', '同步字', NOW()),
('DEMODULATE', 'DEMOD_DESCRAMBLER_SEED', '解扰种子', 'HEX', 0, 65535, 1, 16, 116, 'FFFF', '解扰种子', NOW());

-- 步骤5: 为译码单元添加参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555', NOW()),
('DECODE', 'DEC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '起始地址', NOW()),
('DECODE', 'DEC_SYNC_PATTERN', '同步模式', 'HEX', 0, 4294967295, 1, 32, 115, '1ACFFC1D', '同步模式', NOW()),
('DECODE', 'DEC_DESCRAMBLER_INIT', '解扰初值', 'HEX', 0, 65535, 1, 16, 116, 'FFFF', '解扰初值', NOW());

-- 验证
SELECT '参数添加完成' AS result;
SELECT unit_type, COUNT(*) as count FROM sys_baseband_param_def GROUP BY unit_type;
