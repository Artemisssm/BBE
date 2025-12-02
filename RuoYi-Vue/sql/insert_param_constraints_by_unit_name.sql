-- 基带参数约束规则 - 按单元名称分类
-- 8种单元名称：
-- 1.返向中低速数传 2.返向高速数传 3.ACM数传 4.前向数传小环 
-- 5.低速模拟源 6.高速模拟源 7.ACM模拟源 8.前向数传

-- ============================================
-- 一、返向中低速数传（中低速率，稳定传输）
-- ============================================

-- 1.1 编码单元：编码方式影响码率（中低速适合卷积码和LDPC）
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type, 
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '返向中低速-编码方式影响码率',
    'SELF',
    '返向中低速数传', 'ENCODE', 'ENC_CODE_TYPE',
    '返向中低速数传', 'ENCODE', 'ENC_CODE_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "CONV": {"minValue": 0.5, "maxValue": 0.75, "recommended": 0.5},
            "LDPC": {"minValue": 0.5, "maxValue": 0.8, "recommended": 0.67},
            "TURBO": {"minValue": 0.33, "maxValue": 0.67, "recommended": 0.5}
        }
    }',
    10,
    '中低速数传适合较低码率，保证可靠性'
);

-- 1.2 调制单元：调制方式限制（中低速适合BPSK/QPSK）
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '返向中低速-调制方式限制',
    'SELF',
    '返向中低速数传', 'MODULATE', 'MOD_TYPE',
    '返向中低速数传', 'MODULATE', 'MOD_SYMBOL_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "BPSK": {"minValue": 0.5, "maxValue": 10, "recommended": 2},
            "QPSK": {"minValue": 1, "maxValue": 20, "recommended": 5},
            "8PSK": {"minValue": 2, "maxValue": 30, "disabled": true}
        }
    }',
    10,
    '中低速数传主要使用BPSK/QPSK，符号率较低'
);

-- 1.3 跨单元：编码到调制帧长度同步
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '返向中低速-帧长度同步',
    'CROSS_UNIT',
    '返向中低速数传', 'ENCODE', 'ENC_FRAME_LENGTH',
    '返向中低速数传', 'MODULATE', 'MOD_FRAME_LENGTH',
    '{
        "type": "VALUE_SYNC",
        "formula": "target = source",
        "autoSync": true
    }',
    20,
    '编码和调制帧长度必须一致'
);

-- ============================================
-- 二、返向高速数传（高速率，大数据量）
-- ============================================

-- 2.1 编码单元：高速数传适合高码率
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '返向高速-编码方式影响码率',
    'SELF',
    '返向高速数传', 'ENCODE', 'ENC_CODE_TYPE',
    '返向高速数传', 'ENCODE', 'ENC_CODE_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "LDPC": {"minValue": 0.75, "maxValue": 0.95, "recommended": 0.9},
            "TURBO": {"minValue": 0.67, "maxValue": 0.9, "recommended": 0.8},
            "CONV": {"minValue": 0.67, "maxValue": 0.75, "disabled": true}
        }
    }',
    10,
    '高速数传使用高码率，优先LDPC'
);

-- 2.2 调制单元：高阶调制方式
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '返向高速-调制方式限制',
    'SELF',
    '返向高速数传', 'MODULATE', 'MOD_TYPE',
    '返向高速数传', 'MODULATE', 'MOD_SYMBOL_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "QPSK": {"minValue": 10, "maxValue": 100, "recommended": 50},
            "8PSK": {"minValue": 20, "maxValue": 150, "recommended": 80},
            "16QAM": {"minValue": 30, "maxValue": 200, "recommended": 100}
        }
    }',
    10,
    '高速数传使用高阶调制，符号率较高'
);

-- 2.3 调制单元：功率回退要求
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '返向高速-调制方式影响功率回退',
    'SELF',
    '返向高速数传', 'MODULATE', 'MOD_TYPE',
    '返向高速数传', 'MODULATE', 'MOD_POWER_BACKOFF',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "QPSK": {"minValue": 0, "maxValue": 2, "recommended": 1},
            "8PSK": {"minValue": 2, "maxValue": 4, "recommended": 3},
            "16QAM": {"minValue": 3, "maxValue": 6, "recommended": 4}
        }
    }',
    10,
    '高阶调制需要更大的功率回退'
);

-- ============================================
-- 三、ACM数传（自适应编码调制）
-- ============================================

-- 3.1 编码单元：ACM支持动态码率
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    'ACM数传-启用自适应码率',
    'SELF',
    'ACM数传', 'ENCODE', 'ENC_ACM_ENABLE',
    'ACM数传', 'ENCODE', 'ENC_CODE_RATE',
    '{
        "type": "ENABLE_DISABLE",
        "rules": {
            "1": {"enabled": false, "message": "ACM模式下码率自动调整"},
            "0": {"enabled": true}
        }
    }',
    15,
    'ACM启用时，码率由系统自动调整'
);

-- 3.2 调制单元：ACM支持动态调制
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    'ACM数传-启用自适应调制',
    'SELF',
    'ACM数传', 'MODULATE', 'MOD_ACM_ENABLE',
    'ACM数传', 'MODULATE', 'MOD_TYPE',
    '{
        "type": "ENABLE_DISABLE",
        "rules": {
            "1": {"enabled": false, "message": "ACM模式下调制方式自动调整"},
            "0": {"enabled": true}
        }
    }',
    15,
    'ACM启用时，调制方式由系统自动调整'
);

-- 3.3 跨单元：ACM编码调制联动
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    'ACM数传-编码调制ACM联动',
    'CROSS_UNIT',
    'ACM数传', 'ENCODE', 'ENC_ACM_ENABLE',
    'ACM数传', 'MODULATE', 'MOD_ACM_ENABLE',
    '{
        "type": "VALUE_SYNC",
        "formula": "target = source",
        "autoSync": true,
        "message": "编码和调制ACM状态必须一致"
    }',
    20,
    'ACM模式下编码和调制必须同时启用'
);

-- ============================================
-- 四、前向数传小环（环回测试）
-- ============================================

-- 4.1 编码单元：小环测试使用简单编码
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '前向小环-编码方式限制',
    'SELF',
    '前向数传小环', 'ENCODE', 'ENC_CODE_TYPE',
    '前向数传小环', 'ENCODE', 'ENC_CODE_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "CONV": {"minValue": 0.5, "maxValue": 0.75, "recommended": 0.5},
            "LDPC": {"minValue": 0.5, "maxValue": 0.75, "disabled": true},
            "TURBO": {"minValue": 0.5, "maxValue": 0.67, "disabled": true}
        }
    }',
    10,
    '小环测试优先使用卷积码，简单可靠'
);

-- 4.2 调制单元：小环使用低阶调制
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '前向小环-调制方式限制',
    'SELF',
    '前向数传小环', 'MODULATE', 'MOD_TYPE',
    '前向数传小环', 'MODULATE', 'MOD_SYMBOL_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "BPSK": {"minValue": 0.5, "maxValue": 5, "recommended": 1},
            "QPSK": {"minValue": 1, "maxValue": 10, "recommended": 2}
        }
    }',
    10,
    '小环测试使用低速率，便于调试'
);

-- 4.3 解调译码：小环模式启用环回
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '前向小环-启用环回模式',
    'CROSS_UNIT',
    '前向数传小环', 'MODULATE', 'MOD_LOOPBACK_ENABLE',
    '前向数传小环', 'DEMODULATE', 'DEMOD_LOOPBACK_ENABLE',
    '{
        "type": "VALUE_SYNC",
        "formula": "target = source",
        "autoSync": true,
        "requiredValue": "1"
    }',
    20,
    '小环模式必须启用环回'
);

-- ============================================
-- 五、低速模拟源（低速率信号模拟）
-- ============================================

-- 5.1 调制单元：低速模拟源限制
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '低速模拟源-符号率限制',
    'SELF',
    '低速模拟源', 'MODULATE', 'MOD_TYPE',
    '低速模拟源', 'MODULATE', 'MOD_SYMBOL_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "BPSK": {"minValue": 0.1, "maxValue": 5, "recommended": 1},
            "QPSK": {"minValue": 0.2, "maxValue": 10, "recommended": 2},
            "8PSK": {"minValue": 0.5, "maxValue": 15, "disabled": true}
        }
    }',
    10,
    '低速模拟源符号率范围较小'
);

-- 5.2 调制单元：低速模拟源禁用编码
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '低速模拟源-禁用编码',
    'SELF',
    '低速模拟源', 'MODULATE', 'MOD_SOURCE_TYPE',
    '低速模拟源', 'ENCODE', 'ENC_ENABLE',
    '{
        "type": "ENABLE_DISABLE",
        "rules": {
            "PATTERN": {"enabled": false, "message": "模拟源使用固定码型，不需要编码"},
            "RANDOM": {"enabled": false, "message": "模拟源使用随机数据，不需要编码"}
        }
    }',
    15,
    '模拟源直接产生数据，不需要编码单元'
);

-- ============================================
-- 六、高速模拟源（高速率信号模拟）
-- ============================================

-- 6.1 调制单元：高速模拟源符号率
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '高速模拟源-符号率限制',
    'SELF',
    '高速模拟源', 'MODULATE', 'MOD_TYPE',
    '高速模拟源', 'MODULATE', 'MOD_SYMBOL_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "QPSK": {"minValue": 10, "maxValue": 100, "recommended": 50},
            "8PSK": {"minValue": 20, "maxValue": 150, "recommended": 80},
            "16QAM": {"minValue": 30, "maxValue": 200, "recommended": 100}
        }
    }',
    10,
    '高速模拟源支持高符号率'
);

-- ============================================
-- 七、ACM模拟源（自适应模拟）
-- ============================================

-- 7.1 调制单元：ACM模拟源自适应
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    'ACM模拟源-自适应参数',
    'SELF',
    'ACM模拟源', 'MODULATE', 'MOD_ACM_ENABLE',
    'ACM模拟源', 'MODULATE', 'MOD_TYPE',
    '{
        "type": "ENABLE_DISABLE",
        "rules": {
            "1": {"enabled": false, "message": "ACM模式下调制方式自动变化"},
            "0": {"enabled": true}
        }
    }',
    15,
    'ACM模拟源可模拟自适应调制变化'
);

-- ============================================
-- 八、前向数传（标准前向链路）
-- ============================================

-- 8.1 编码单元：前向数传编码配置
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '前向数传-编码方式影响码率',
    'SELF',
    '前向数传', 'ENCODE', 'ENC_CODE_TYPE',
    '前向数传', 'ENCODE', 'ENC_CODE_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "LDPC": {"minValue": 0.5, "maxValue": 0.9, "recommended": 0.75},
            "TURBO": {"minValue": 0.33, "maxValue": 0.9, "recommended": 0.67},
            "CONV": {"minValue": 0.5, "maxValue": 0.75, "recommended": 0.5}
        }
    }',
    10,
    '前向数传支持多种编码方式和码率'
);

-- 8.2 调制单元：前向数传调制配置
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '前向数传-调制方式限制',
    'SELF',
    '前向数传', 'MODULATE', 'MOD_TYPE',
    '前向数传', 'MODULATE', 'MOD_SYMBOL_RATE',
    '{
        "type": "VALUE_RANGE",
        "rules": {
            "BPSK": {"minValue": 1, "maxValue": 20, "recommended": 5},
            "QPSK": {"minValue": 2, "maxValue": 50, "recommended": 10},
            "8PSK": {"minValue": 5, "maxValue": 100, "recommended": 20},
            "16QAM": {"minValue": 10, "maxValue": 150, "recommended": 50}
        }
    }',
    10,
    '前向数传支持多种调制方式'
);

-- ============================================
-- 通用跨单元约束（适用于所有数传单元）
-- ============================================

-- 解调到译码：软判决联动
INSERT INTO sys_baseband_param_constraint (
    constraint_name, constraint_type,
    source_unit_name, source_unit_type, source_param_code,
    target_unit_name, target_unit_type, target_param_code,
    constraint_rule, priority, remark
) VALUES (
    '通用-解调方式影响译码软判决',
    'CROSS_UNIT',
    NULL, 'DEMODULATE', 'DEMOD_TYPE',
    NULL, 'DECODE', 'DEC_SOFT_DECISION',
    '{
        "type": "ENABLE_DISABLE",
        "rules": {
            "COHERENT": {"enabled": true, "value": "1", "message": "相干解调支持软判决"},
            "NON_COHERENT": {"enabled": true, "value": "0", "message": "非相干解调建议硬判决"}
        }
    }',
    5,
    '通用规则：解调方式影响译码软判决，适用于所有单元'
);

-- 查询约束规则统计
SELECT 
    source_unit_name,
    constraint_type,
    COUNT(*) as constraint_count
FROM sys_baseband_param_constraint
WHERE enabled = '1'
GROUP BY source_unit_name, constraint_type
ORDER BY source_unit_name, constraint_type;
