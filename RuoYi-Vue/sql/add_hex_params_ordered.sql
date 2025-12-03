-- ========================================
-- 有序添加16进制参数
-- 帧头标志和起始地址在最前面
-- ========================================

-- 步骤1: 删除已存在的HEX类型参数（如果有）
-- 先查看要删除的参数
SELECT CONCAT('准备删除 ', COUNT(*), ' 个HEX参数') AS info
FROM sys_baseband_param_def WHERE value_type = 'HEX';

-- 删除
DELETE FROM sys_baseband_param_def WHERE value_type = 'HEX';

SELECT '已删除旧的HEX参数' AS result;

-- 步骤2: 按顺序插入参数（先插入的param_id小，会排在前面）

-- 编码单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555，2字节', NOW()),
('ENCODE', 'ENC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '参数起始地址，4字节', NOW());

-- 调制单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555，2字节', NOW()),
('MODULATE', 'MOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '参数起始地址，4字节', NOW());

-- 解调单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555，2字节', NOW()),
('DEMODULATE', 'DEMOD_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '参数起始地址，4字节', NOW());

-- 译码单元 - 帧头和起始地址
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_FRAME_HEADER', '帧头标志', 'HEX', 0, 65535, 1, 'U16', '5555', '固定值0x5555，2字节', NOW()),
('DECODE', 'DEC_START_ADDR', '起始地址', 'HEX', 0, 4294967295, 1, 'U32', '00000000', '参数起始地址，4字节', NOW());

-- 步骤3: 插入其他16进制参数

-- 编码单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('ENCODE', 'ENC_SYNC_WORD_HEX', '同步字', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '帧同步字，32位', NOW()),
('ENCODE', 'ENC_SCRAMBLER_SEED', '扰码种子', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '扰码器初始种子', NOW()),
('ENCODE', 'ENC_CRC_POLY', 'CRC多项式', 'HEX', 0, 4294967295, 1, 'U32', '04C11DB7', 'CRC32多项式', NOW()),
('ENCODE', 'ENC_MASK_PATTERN', '掩码模式', 'HEX', 0, 255, 1, 'U8', 'AA', '数据掩码，8位', NOW());

-- 调制单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('MODULATE', 'MOD_PILOT_PATTERN', '导频图案', 'HEX', 0, 4294967295, 1, 'U32', '12345678', '导频插入模式', NOW()),
('MODULATE', 'MOD_SCRAMBLER_INIT', '扰码初值', 'HEX', 0, 65535, 1, 'U16', '0001', '扰码器初始化值', NOW()),
('MODULATE', 'MOD_SYNC_MARKER', '同步标记', 'HEX', 0, 255, 1, 'U8', '47', 'TS同步字节', NOW()),
('MODULATE', 'MOD_FRAME_ID', '帧标识', 'HEX', 0, 65535, 1, 'U16', 'ABCD', '帧ID标识', NOW());

-- 解调单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DEMODULATE', 'DEMOD_SYNC_WORD', '同步字', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '帧同步检测字', NOW()),
('DEMODULATE', 'DEMOD_DESCRAMBLER_SEED', '解扰种子', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '解扰码器种子', NOW()),
('DEMODULATE', 'DEMOD_PILOT_MASK', '导频掩码', 'HEX', 0, 4294967295, 1, 'U32', 'FFFFFFFF', '导频提取掩码', NOW()),
('DEMODULATE', 'DEMOD_CARRIER_OFFSET', '载波偏移', 'HEX', 0, 65535, 1, 'U16', '0000', '载波频偏补偿', NOW());

-- 译码单元 - 其他参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, min_value, max_value, quantization_unit, bit_width_type, default_value, remark, create_time)
VALUES
('DECODE', 'DEC_SYNC_PATTERN', '同步模式', 'HEX', 0, 4294967295, 1, 'U32', '1ACFFC1D', '帧同步检测模式', NOW()),
('DECODE', 'DEC_DESCRAMBLER_INIT', '解扰初值', 'HEX', 0, 65535, 1, 'U16', 'FFFF', '解扰码器初始值', NOW()),
('DECODE', 'DEC_CRC_CHECK', 'CRC校验值', 'HEX', 0, 4294967295, 1, 'U32', '04C11DB7', 'CRC校验多项式', NOW()),
('DECODE', 'DEC_ERROR_MASK', '错误掩码', 'HEX', 0, 255, 1, 'U8', '00', '错误检测掩码', NOW());

-- 验证结果
SELECT '参数添加完成！' AS result;
SELECT '每个单元类型添加了6个16进制参数（2个帧头+4个其他）' AS info;

-- 查看参数数量
SELECT unit_type, COUNT(*) as total_count,
       SUM(CASE WHEN value_type = 'HEX' THEN 1 ELSE 0 END) as hex_count
FROM sys_baseband_param_def 
GROUP BY unit_type
ORDER BY unit_type;

-- 查看HEX参数（按param_id排序，帧头和起始地址应该在前面）
SELECT param_id, unit_type, param_code, param_name, bit_width_type, default_value
FROM sys_baseband_param_def
WHERE value_type = 'HEX'
ORDER BY unit_type, param_id;
