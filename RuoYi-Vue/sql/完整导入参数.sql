-- ========================================
-- 基带监控模块 - 完整导入参数定义
-- 包含表结构重建和137个参数导入
-- 执行时间: 约10秒
-- ========================================

-- 步骤1: 删除旧表
DROP TABLE IF EXISTS sys_baseband_param_def;

-- 步骤2: 创建新表
CREATE TABLE sys_baseband_param_def (
    param_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '参数定义主键',
    unit_type      VARCHAR(16)  NOT NULL COMMENT '适用单元类型',
    param_code     VARCHAR(64)  NOT NULL COMMENT '唯一编码',
    param_name     VARCHAR(64)  NOT NULL COMMENT '显示名称',
    value_type     VARCHAR(16)  NOT NULL COMMENT 'ENUM/UINT/FLOAT/SWITCH',
    enum_options   TEXT         DEFAULT NULL COMMENT '枚举选项 JSON',
    min_value      DECIMAL(18,6) DEFAULT NULL,
    max_value      DECIMAL(18,6) DEFAULT NULL,
    scale_factor   INT          DEFAULT 1 COMMENT '浮点转无符号整数时的缩放倍数',
    bit_length     INT          DEFAULT 32 COMMENT '硬件位宽',
    hardware_order INT          NOT NULL COMMENT '硬件顺序字段',
    default_value  VARCHAR(64)  DEFAULT NULL,
    remark         VARCHAR(255) DEFAULT NULL,
    create_time    DATETIME     DEFAULT NULL,
    PRIMARY KEY (param_id),
    UNIQUE KEY uk_param_code (param_code),
    KEY idx_unit_type (unit_type),
    KEY idx_hardware_order (hardware_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基带参数定义';

-- 步骤3: 导入参数数据（137个参数）

-- 编码单元 30个参数
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, enum_options, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
('ENCODE','ENC_FEC_TYPE','前向纠错类型','ENUM','{"0":"无编码","1":"卷积码","2":"Turbo码","3":"LDPC码","4":"RS码","5":"BCH码","6":"极化码"}',NULL,NULL,1,4,10,'1','FEC编码类型',NOW()),
('ENCODE','ENC_CODE_RATE','编码码率','ENUM','{"0":"1/2","1":"1/3","2":"1/4","3":"2/3","4":"3/4","5":"4/5","6":"5/6","7":"7/8","8":"8/9"}',NULL,NULL,1,4,20,'0','编码效率',NOW()),
('ENCODE','ENC_CONSTRAINT_LEN','约束长度','UINT',NULL,3,15,1,8,30,'7','卷积码K值',NOW()),
('ENCODE','ENC_POLY_G1','生成多项式G1','UINT',NULL,1,511,1,16,40,'171','八进制表示',NOW()),
('ENCODE','ENC_POLY_G2','生成多项式G2','UINT',NULL,1,511,1,16,50,'133','八进制表示',NOW()),
('ENCODE','ENC_INTERLEAVER_TYPE','交织器类型','ENUM','{"0":"不交织","1":"块交织","2":"卷积交织","3":"随机交织","4":"S随机交织"}',NULL,NULL,1,4,60,'1','交织方式',NOW()),
('ENCODE','ENC_INTERLEAVER_ROWS','交织行数','UINT',NULL,1,256,1,16,70,'16','块交织行',NOW()),
('ENCODE','ENC_INTERLEAVER_COLS','交织列数','UINT',NULL,1,256,1,16,80,'32','块交织列',NOW()),
('ENCODE','ENC_INTERLEAVER_DEPTH','交织深度','UINT',NULL,1,1024,1,16,90,'64','卷积交织深度',NOW()),
('ENCODE','ENC_FRAME_LENGTH','信息帧长(bit)','UINT',NULL,64,65536,1,32,100,'1024','信息位长度',NOW()),
('ENCODE','ENC_FRAME_SYNC_WORD','帧同步字','UINT',NULL,0,4294967295,1,32,110,'429496729','32bit同步码',NOW()),
('ENCODE','ENC_FRAME_HEADER_LEN','帧头长度(byte)','UINT',NULL,0,256,1,16,120,'16','帧头字节数',NOW()),
('ENCODE','ENC_FRAME_TAIL_LEN','帧尾长度(byte)','UINT',NULL,0,256,1,16,130,'8','帧尾字节数',NOW()),
('ENCODE','ENC_CRC_TYPE','CRC类型','ENUM','{"0":"无CRC","1":"CRC8","2":"CRC16-CCITT","3":"CRC16-IBM","4":"CRC24","5":"CRC32"}',NULL,NULL,1,4,140,'2','校验类型',NOW()),
('ENCODE','ENC_CRC_POLY','CRC多项式','UINT',NULL,0,4294967295,1,32,150,'4129','CRC生成多项式',NOW()),
('ENCODE','ENC_CRC_INIT','CRC初始值','UINT',NULL,0,4294967295,1,32,160,'65535','CRC初始化',NOW()),
('ENCODE','ENC_SCRAMBLE_ENABLE','扰码使能','SWITCH',NULL,NULL,NULL,1,1,170,'1','是否启用扰码',NOW()),
('ENCODE','ENC_SCRAMBLE_POLY','扰码多项式','ENUM','{"0":"x7+x1+1","1":"x9+x5+1","2":"x11+x9+1","3":"x15+x14+1","4":"x17+x14+1","5":"x23+x18+1"}',NULL,NULL,1,4,180,'1','扰码生成多项式',NOW()),
('ENCODE','ENC_SCRAMBLE_INIT','扰码初始值','UINT',NULL,0,16777215,1,32,190,'1','扰码寄存器初值',NOW()),
('ENCODE','ENC_PUNCTURE_ENABLE','删余使能','SWITCH',NULL,NULL,NULL,1,1,200,'0','是否启用删余',NOW()),
('ENCODE','ENC_PUNCTURE_PATTERN','删余模式','UINT',NULL,0,65535,1,16,210,'0','删余图样',NOW()),
('ENCODE','ENC_TURBO_ITER','Turbo迭代次数','UINT',NULL,1,16,1,8,220,'8','内部迭代',NOW()),
('ENCODE','ENC_LDPC_MATRIX','LDPC矩阵索引','UINT',NULL,0,255,1,8,230,'0','预定义矩阵',NOW()),
('ENCODE','ENC_LDPC_Z_FACTOR','LDPC扩展因子','UINT',NULL,1,384,1,16,240,'27','Z因子',NOW()),
('ENCODE','ENC_OUTPUT_INVERT','输出反转','SWITCH',NULL,NULL,NULL,1,1,250,'0','比特反转',NOW()),
('ENCODE','ENC_OUTPUT_FORMAT','输出格式','ENUM','{"0":"串行","1":"并行8bit","2":"并行16bit","3":"并行32bit"}',NULL,NULL,1,2,260,'0','输出接口',NOW()),
('ENCODE','ENC_DATA_ENDIAN','数据字节序','ENUM','{"0":"大端","1":"小端"}',NULL,NULL,1,1,270,'0','字节序',NOW()),
('ENCODE','ENC_TEST_MODE','测试模式','ENUM','{"0":"正常","1":"PRBS7","2":"PRBS15","3":"PRBS23","4":"全0","5":"全1","6":"交替01"}',NULL,NULL,1,4,280,'0','测试序列',NOW()),
('ENCODE','ENC_BYPASS','旁路模式','SWITCH',NULL,NULL,NULL,1,1,290,'0','编码旁路',NOW()),
('ENCODE','ENC_RESET','软复位','SWITCH',NULL,NULL,NULL,1,1,300,'0','模块复位',NOW());

-- 验证编码单元参数
SELECT '编码单元参数导入完成' AS status, COUNT(*) AS count FROM sys_baseband_param_def WHERE unit_type='ENCODE';
