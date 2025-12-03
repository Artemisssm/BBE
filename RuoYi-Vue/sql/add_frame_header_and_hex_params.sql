-- ========================================
-- 添加帧头标志、起始地址和16进制参数
-- 为四个单元类型（ENCODE、MODULATE、DEMODULATE、DECODE）添加
-- ========================================

-- 为每个单元类型添加帧头标志和起始地址（hardware_order = 1, 2）
-- 然后将原有参数的hardware_order都加2

-- 步骤1: 更新现有参数的hardware_order（为帧头和起始地址腾出位置）
UPDATE sys_baseband_param_def 
SET hardware_order = hardware_order + 2 
WHERE unit_type IN ('ENCODE', 'MODULATE', 'DEMODULATE', 'DECODE');

-- 步骤2: 为编码单元添加帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555，2字节', NOW()),
('ENCODE', 'ENC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '参数起始地址，4字节', NOW());

-- 步骤3: 为调制单元添加帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555，2字节', NOW()),
('MODULATE', 'MOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '参数起始地址，4字节', NOW());

-- 步骤4: 为解调单元添加帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555，2字节', NOW()),
('DEMODULATE', 'DEMOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '参数起始地址，4字节', NOW());

-- 步骤5: 为译码单元添加帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 16, 1, '5555', '固定值0x5555，2字节', NOW()),
('DECODE', 'DEC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 32, 2, '00000000', '参数起始地址，4字节', NOW());

-- 步骤6: 为编码单元添加一些16进制类型的参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_SYNC_WORD_HEX', '同步字(HEX)', 'HEX', 0, 4294967295, 1, 32, 115, '1ACFFC1D', '帧同步字，32位16进制', NOW()),
('ENCODE', 'ENC_SCRAMBLER_SEED', '扰码种子', 'HEX', 0, 65535, 1, 16, 116, 'FFFF', '扰码器初始种子', NOW()),
('ENCODE', 'ENC_CRC_POLY', 'CRC多项式', 'HEX', 0, 4294967295, 1, 32, 117, '04C11DB7', 'CRC32多项式', NOW()),
('ENCODE', 'ENC_MASK_PATTERN', '掩码模式', 'HEX', 0, 255, 1, 8, 118, 'AA', '数据掩码，8位', NOW());

-- 步骤7: 为调制单元添加一些16进制类型的参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_PILOT_PATTERN', '导频图案', 'HEX', 0, 4294967295, 1, 32, 115, '12345678', '导频插入模式', NOW()),
('MODULATE', 'MOD_SCRAMBLER_INIT', '扰码初值', 'HEX', 0, 65535, 1, 16, 116, '0001', '扰码器初始化值', NOW()),
('MODULATE', 'MOD_SYNC_MARKER', '同步标记', 'HEX', 0, 255, 1, 8, 117, '47', 'TS同步字节', NOW()),
('MODULATE', 'MOD_FRAME_ID', '帧标识', 'HEX', 0, 65535, 1, 16, 118, 'ABCD', '帧ID标识', NOW());

-- 步骤8: 为解调单元添加一些16进制类型的参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_SYNC_WORD', '同步字', 'HEX', 0, 4294967295, 1, 32, 115, '1ACFFC1D', '帧同步检测字', NOW()),
('DEMODULATE', 'DEMOD_DESCRAMBLER_SEED', '解扰种子', 'HEX', 0, 65535, 1, 16, 116, 'FFFF', '解扰码器种子', NOW()),
('DEMODULATE', 'DEMOD_PILOT_MASK', '导频掩码', 'HEX', 0, 4294967295, 1, 32, 117, 'FFFFFFFF', '导频提取掩码', NOW()),
('DEMODULATE', 'DEMOD_CARRIER_OFFSET', '载波偏移', 'HEX', 0, 65535, 1, 16, 118, '0000', '载波频偏补偿', NOW());

-- 步骤9: 为译码单元添加一些16进制类型的参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_SYNC_PATTERN', '同步模式', 'HEX', 0, 4294967295, 1, 32, 115, '1ACFFC1D', '帧同步检测模式', NOW()),
('DECODE', 'DEC_DESCRAMBLER_INIT', '解扰初值', 'HEX', 0, 65535, 1, 16, 116, 'FFFF', '解扰码器初始值', NOW()),
('DECODE', 'DEC_CRC_CHECK', 'CRC校验值', 'HEX', 0, 4294967295, 1, 32, 117, '04C11DB7', 'CRC校验多项式', NOW()),
('DECODE', 'DEC_ERROR_MASK', '错误掩码', 'HEX', 0, 255, 1, 8, 118, '00', '错误检测掩码', NOW());

-- 验证结果
SELECT '帧头和16进制参数添加完成' AS result;
SELECT unit_type, COUNT(*) as param_count 
FROM sys_baseband_param_def 
GROUP BY unit_type 
ORDER BY unit_type;

-- 查看新添加的参数（hardware_order <= 5 或 >= 115）
SELECT unit_type, param_code, param_name, value_type, default_value, hardware_order
FROM sys_baseband_param_def
WHERE hardware_order <= 5 OR hardware_order >= 115
ORDER BY unit_type, hardware_order;
