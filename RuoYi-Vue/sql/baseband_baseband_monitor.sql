-- 基带监控模块 SQL 脚本
-- 创建日期：2025-11-27
-- 说明：包含基带单元、参数定义、参数取值以及下发日志四张表

DROP TABLE IF EXISTS sys_baseband_dispatch_log;
DROP TABLE IF EXISTS sys_baseband_param_value;
DROP TABLE IF EXISTS sys_baseband_param_def;
DROP TABLE IF EXISTS sys_baseband_unit;

CREATE TABLE sys_baseband_unit (
    unit_id        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    unit_name      VARCHAR(64)  NOT NULL COMMENT '单元名称',
    unit_type      VARCHAR(16)  NOT NULL COMMENT 'ENCODE/MODULATE/DEMODULATE/DECODE',
    channel_no     INT          NOT NULL DEFAULT 1 COMMENT '通道号',
    status         CHAR(1)      DEFAULT '0' COMMENT '0正常 1停用',
    version        VARCHAR(32)  DEFAULT NULL COMMENT '硬件/配置版本',
    remark         VARCHAR(255) DEFAULT NULL,
    create_by      VARCHAR(64)  DEFAULT NULL,
    create_time    DATETIME     DEFAULT NULL,
    update_by      VARCHAR(64)  DEFAULT NULL,
    update_time    DATETIME     DEFAULT NULL,
    PRIMARY KEY (unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带单元';

CREATE TABLE sys_baseband_param_def (
    param_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '参数定义主键',
    unit_type      VARCHAR(16)  NOT NULL COMMENT '适用单元类型',
    param_code     VARCHAR(64)  NOT NULL COMMENT '唯一编码',
    param_name     VARCHAR(64)  NOT NULL COMMENT '显示名称',
    value_type     VARCHAR(16)  NOT NULL COMMENT 'ENUM/UINT/FLOAT/SWITCH',
    enum_options   JSON         DEFAULT NULL COMMENT '枚举选项 JSON',
    min_value      DECIMAL(18,6) DEFAULT NULL,
    max_value      DECIMAL(18,6) DEFAULT NULL,
    scale_factor   INT          DEFAULT 1 COMMENT '浮点转无符号整数时的缩放倍数',
    bit_length     INT          DEFAULT 32 COMMENT '硬件位宽',
    hardware_order INT          NOT NULL COMMENT '硬件顺序字段',
    default_value  VARCHAR(64)  DEFAULT NULL,
    remark         VARCHAR(255) DEFAULT NULL,
    create_time    DATETIME     DEFAULT NULL,
    PRIMARY KEY (param_id),
    UNIQUE KEY uk_param_code (param_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带参数定义';

CREATE TABLE sys_baseband_param_value (
    value_id    BIGINT      NOT NULL AUTO_INCREMENT COMMENT '参数值主键',
    unit_id     BIGINT      NOT NULL COMMENT '所属单元',
    param_id    BIGINT      NOT NULL COMMENT '参数定义',
    raw_value   VARCHAR(64) NOT NULL COMMENT '用户输入原值（字符串表示）',
    uint_value  BIGINT      NOT NULL COMMENT '转换后的无符号整型',
    update_by   VARCHAR(64) DEFAULT NULL,
    update_time DATETIME    DEFAULT NULL,
    PRIMARY KEY (value_id),
    UNIQUE KEY uk_unit_param (unit_id, param_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带参数取值';

CREATE TABLE sys_baseband_dispatch_log (
    log_id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    unit_id        BIGINT      NOT NULL COMMENT '下发单元',
    dispatch_type  VARCHAR(16) DEFAULT 'MANUAL' COMMENT 'MANUAL/AUTO',
    multicast_ip   VARCHAR(32) DEFAULT NULL,
    multicast_port INT         DEFAULT NULL,
    packet_len     INT         DEFAULT NULL COMMENT '报文长度',
    result_status  CHAR(1)     NOT NULL COMMENT '0成功 1失败',
    result_msg     VARCHAR(255) DEFAULT NULL,
    send_time      DATETIME     DEFAULT NULL,
    create_by      VARCHAR(64)  DEFAULT NULL,
    PRIMARY KEY (log_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '基带参数下发日志';

-- 初始化参数定义数据
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, enum_options, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
-- 调制单元
('MODULATE','MOD_SCHEME','调制体制','ENUM','[{"label":"BPSK","value":0},{"label":"QPSK","value":1},{"label":"OQPSK","value":2},{"label":"8PSK","value":3},{"label":"16QAM","value":4},{"label":"GMSK","value":5}]',NULL,NULL,1,8,10,'0','枚举：0=BPSK',NOW()),
('MODULATE','SYM_RATE','符号率(MSym/s)','FLOAT',NULL,0.050,80.000,1000000,32,20,'10000000','单位 Msps，乘1e6写入',NOW()),
('MODULATE','IF_FREQ','中频频率(MHz)','FLOAT',NULL,10.000,400.000,1000,32,30,'70000','单位 MHz，乘1e3写入',NOW()),
('MODULATE','ROLL_OFF','滚降系数','ENUM','[{"label":"0.20","value":0},{"label":"0.25","value":1},{"label":"0.35","value":2},{"label":"0.50","value":3}]',NULL,NULL,1,4,40,'2','0:0.20 1:0.25 2:0.35 3:0.50',NOW()),
('MODULATE','OUT_POWER','输出功率(dBm)','FLOAT',NULL,-30.000,10.000,10,16,50,'0','乘10转整型，dBm',NOW()),
('MODULATE','PHASE_OFFSET','相位偏置(度)','FLOAT',NULL,-180.000,180.000,100,16,60,'0','乘100记录，0.01度分辨率',NOW()),
('MODULATE','PRE_EMPHASIS','预加重系数','FLOAT',NULL,0.000,1.000,10000,16,70,'0','0~1，四位小数',NOW()),
('MODULATE','CLOCK_SOURCE','时钟源','ENUM','[{"label":"内部晶振","value":0},{"label":"外部10MHz","value":1},{"label":"GPSDO","value":2}]',NULL,NULL,1,2,80,'0','0=内部 1=外部 2=GPSDO',NOW()),
('MODULATE','PA_ENABLE','功放使能','SWITCH',NULL,NULL,NULL,1,1,90,'0','0关 1开',NOW()),
-- 编码单元
('ENCODE','FEC_TYPE','编码方案','ENUM','[{"label":"卷积","value":0},{"label":"Turbo","value":1},{"label":"LDPC","value":2},{"label":"RS","value":3},{"label":"BCH","value":4}]',NULL,NULL,1,4,10,'0','0=卷积 1=Turbo 2=LDPC等',NOW()),
('ENCODE','CODE_RATE','码率','ENUM','[{"label":"1/2","value":0},{"label":"2/3","value":1},{"label":"3/4","value":2},{"label":"5/6","value":3},{"label":"7/8","value":4}]',NULL,NULL,1,4,20,'0','常用码率',NOW()),
('ENCODE','CONSTRAINT_LEN','约束长度','UINT',NULL,3,15,1,8,30,'7','卷积编码K值',NOW()),
('ENCODE','INTERLEAVER','交织方式','ENUM','[{"label":"不交织","value":0},{"label":"块交织","value":1},{"label":"卷积交织","value":2},{"label":"伪随机","value":3}]',NULL,NULL,1,4,40,'0','交织策略',NOW()),
('ENCODE','INTERLEAVER_DEPTH','交织深度','UINT',NULL,1,512,1,16,50,'8','块/卷积交织深度',NOW()),
('ENCODE','FRAME_LENGTH','帧长(bit)','UINT',NULL,128,32768,1,16,60,'1024','支持变帧',NOW()),
('ENCODE','CRC_POLY','CRC多项式','ENUM','[{"label":"CRC16","value":0},{"label":"CRC24","value":1},{"label":"CRC32","value":2}]',NULL,NULL,1,2,70,'0','0=CRC16等',NOW()),
('ENCODE','SCRAMBLE_POLY','扰码多项式','ENUM','[{"label":"x^7+x^1+1","value":0},{"label":"x^9+x^5+1","value":1},{"label":"x^15+x^14+1","value":2}]',NULL,NULL,1,2,80,'0','扰码配置',NOW()),
('ENCODE','TEST_PATTERN','测试序列','ENUM','[{"label":"关闭","value":0},{"label":"PRBS7","value":1},{"label":"PRBS15","value":2},{"label":"固定字","value":3}]',NULL,NULL,1,2,90,'0','测试/回环模式',NOW()),
-- 解调单元
('DEMODULATE','DEMOD_SCHEME','解调体制','ENUM','[{"label":"BPSK","value":0},{"label":"QPSK","value":1},{"label":"OQPSK","value":2},{"label":"8PSK","value":3},{"label":"16QAM","value":4},{"label":"自动","value":5}]',NULL,NULL,1,4,10,'5','5=自动识别',NOW()),
('DEMODULATE','INPUT_BW','输入带宽(MHz)','FLOAT',NULL,2.000,100.000,1000,16,20,'20000','乘1e3',NOW()),
('DEMODULATE','AGC_TARGET','AGC目标功率(dBm)','FLOAT',NULL,-80.000,-10.000,100,16,30,'-4000','乘100',NOW()),
('DEMODULATE','CARRIER_LOOP_BW','载波环路带宽(Hz)','FLOAT',NULL,1.000,5000.000,100,32,40,'1000','乘100',NOW()),
('DEMODULATE','SYMBOL_LOOP_BW','符号环路带宽(Hz)','FLOAT',NULL,0.100,2000.000,1000,32,50,'500','乘1000',NOW()),
('DEMODULATE','FREQ_OFFSET_RANGE','可补偿频偏(Hz)','UINT',NULL,100,200000,1,32,60,'50000','最大频偏',NOW()),
('DEMODULATE','IQ_CAL_ENABLE','I/Q校准','SWITCH',NULL,NULL,NULL,1,1,70,'1','默认启用',NOW()),
('DEMODULATE','CNO_THRESHOLD','C/N0告警阈值(dB-Hz)','FLOAT',NULL,20.000,60.000,10,8,80,'350','乘10',NOW()),
('DEMODULATE','LOCK_TIMEOUT','锁定超时(ms)','UINT',NULL,10,5000,1,16,90,'500','失锁超时',NOW()),
-- 译码单元
('DECODE','DECODER_MODE','译码模式','ENUM','[{"label":"Viterbi","value":0},{"label":"Turbo","value":1},{"label":"LDPC","value":2},{"label":"RS","value":3},{"label":"BCH","value":4}]',NULL,NULL,1,4,10,'0','对应编码方案',NOW()),
('DECODE','ITER_LIMIT','迭代次数','UINT',NULL,1,32,1,8,20,'8','Turbo/LDPC迭代',NOW()),
('DECODE','EARLY_STOP','早停开关','SWITCH',NULL,NULL,NULL,1,1,30,'1','LLR早停',NOW()),
('DECODE','SOFT_WIDTH','软判决位宽(bit)','UINT',NULL,2,10,1,4,40,'6','I/Q量化宽度',NOW()),
('DECODE','FRAME_SYNC_TOL','帧同步容错(bit)','UINT',NULL,0,16,1,5,50,'2','允许错位',NOW()),
('DECODE','OUTPUT_FORMAT','输出帧格式','ENUM','[{"label":"裸Payload","value":0},{"label":"含帧头","value":1},{"label":"含时间戳","value":2}]',NULL,NULL,1,2,60,'1','0/1/2',NOW()),
('DECODE','STAT_WINDOW','误码统计窗口(帧)','UINT',NULL,10,10000,1,16,70,'100','FER/BER窗口',NOW()),
('DECODE','BYPASS_MODE','旁路模式','ENUM','[{"label":"正常译码","value":0},{"label":"译码前回环","value":1},{"label":"译码后直通","value":2}]',NULL,NULL,1,2,80,'0','调试使用',NOW());

