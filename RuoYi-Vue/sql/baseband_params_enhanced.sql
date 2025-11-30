-- ========================================
-- 基带监控模块 - 增强版参数定义
-- 包含四个单元的完整参数配置
-- 创建时间: 2025-11-29
-- 参数总数: 137个
-- ========================================

-- 清空现有参数定义
TRUNCATE TABLE sys_baseband_param_def;

-- ========================================
-- 一、编码单元 ENCODE - 30个参数
-- ========================================
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, enum_options, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
-- 基础编码参数 5个
('ENCODE','ENC_FEC_TYPE','前向纠错类型','ENUM','{"0":"无编码","1":"卷积码","2":"Turbo码","3":"LDPC码","4":"RS码","5":"BCH码","6":"极化码"}',NULL,NULL,1,4,10,'1','FEC编码类型',NOW()),
('ENCODE','ENC_CODE_RATE','编码码率','ENUM','{"0":"1/2","1":"1/3","2":"1/4","3":"2/3","4":"3/4","5":"4/5","6":"5/6","7":"7/8","8":"8/9"}',NULL,NULL,1,4,20,'0','编码效率',NOW()),
('ENCODE','ENC_CONSTRAINT_LEN','约束长度','UINT',NULL,3,15,1,8,30,'7','卷积码K值',NOW()),
('ENCODE','ENC_POLY_G1','生成多项式G1','UINT',NULL,1,511,1,16,40,'171','八进制表示',NOW()),
('ENCODE','ENC_POLY_G2','生成多项式G2','UINT',NULL,1,511,1,16,50,'133','八进制表示',NOW()),
-- 交织参数 4个
('ENCODE','ENC_INTERLEAVER_TYPE','交织器类型','ENUM','{"0":"不交织","1":"块交织","2":"卷积交织","3":"随机交织","4":"S随机交织"}',NULL,NULL,1,4,60,'1','交织方式',NOW()),
('ENCODE','ENC_INTERLEAVER_ROWS','交织行数','UINT',NULL,1,256,1,16,70,'16','块交织行',NOW()),
('ENCODE','ENC_INTERLEAVER_COLS','交织列数','UINT',NULL,1,256,1,16,80,'32','块交织列',NOW()),
('ENCODE','ENC_INTERLEAVER_DEPTH','交织深度','UINT',NULL,1,1024,1,16,90,'64','卷积交织深度',NOW()),
-- 帧结构参数 4个
('ENCODE','ENC_FRAME_LENGTH','信息帧长(bit)','UINT',NULL,64,65536,1,32,100,'1024','信息位长度',NOW()),
('ENCODE','ENC_FRAME_SYNC_WORD','帧同步字','UINT',NULL,0,4294967295,1,32,110,'429496729','32bit同步码',NOW()),
('ENCODE','ENC_FRAME_HEADER_LEN','帧头长度(byte)','UINT',NULL,0,256,1,16,120,'16','帧头字节数',NOW()),
('ENCODE','ENC_FRAME_TAIL_LEN','帧尾长度(byte)','UINT',NULL,0,256,1,16,130,'8','帧尾字节数',NOW()),
-- CRC参数 3个
('ENCODE','ENC_CRC_TYPE','CRC类型','ENUM','{"0":"无CRC","1":"CRC8","2":"CRC16-CCITT","3":"CRC16-IBM","4":"CRC24","5":"CRC32"}',NULL,NULL,1,4,140,'2','校验类型',NOW()),
('ENCODE','ENC_CRC_POLY','CRC多项式','UINT',NULL,0,4294967295,1,32,150,'4129','CRC生成多项式',NOW()),
('ENCODE','ENC_CRC_INIT','CRC初始值','UINT',NULL,0,4294967295,1,32,160,'65535','CRC初始化',NOW()),
-- 扰码参数 3个
('ENCODE','ENC_SCRAMBLE_ENABLE','扰码使能','SWITCH',NULL,NULL,NULL,1,1,170,'1','是否启用扰码',NOW()),
('ENCODE','ENC_SCRAMBLE_POLY','扰码多项式','ENUM','{"0":"x7+x1+1","1":"x9+x5+1","2":"x11+x9+1","3":"x15+x14+1","4":"x17+x14+1","5":"x23+x18+1"}',NULL,NULL,1,4,180,'1','扰码生成多项式',NOW()),
('ENCODE','ENC_SCRAMBLE_INIT','扰码初始值','UINT',NULL,0,16777215,1,32,190,'1','扰码寄存器初值',NOW()),
-- 穿孔参数 2个
('ENCODE','ENC_PUNCTURE_ENABLE','删余使能','SWITCH',NULL,NULL,NULL,1,1,200,'0','是否启用删余',NOW()),
('ENCODE','ENC_PUNCTURE_PATTERN','删余模式','UINT',NULL,0,65535,1,16,210,'0','删余图样',NOW()),
-- Turbo LDPC专用参数 3个
('ENCODE','ENC_TURBO_ITER','Turbo迭代次数','UINT',NULL,1,16,1,8,220,'8','内部迭代',NOW()),
('ENCODE','ENC_LDPC_MATRIX','LDPC矩阵索引','UINT',NULL,0,255,1,8,230,'0','预定义矩阵',NOW()),
('ENCODE','ENC_LDPC_Z_FACTOR','LDPC扩展因子','UINT',NULL,1,384,1,16,240,'27','Z因子',NOW()),
-- 输出控制 3个
('ENCODE','ENC_OUTPUT_INVERT','输出反转','SWITCH',NULL,NULL,NULL,1,1,250,'0','比特反转',NOW()),
('ENCODE','ENC_OUTPUT_FORMAT','输出格式','ENUM','{"0":"串行","1":"并行8bit","2":"并行16bit","3":"并行32bit"}',NULL,NULL,1,2,260,'0','输出接口',NOW()),
('ENCODE','ENC_DATA_ENDIAN','数据字节序','ENUM','{"0":"大端","1":"小端"}',NULL,NULL,1,1,270,'0','字节序',NOW()),
-- 测试与调试 3个
('ENCODE','ENC_TEST_MODE','测试模式','ENUM','{"0":"正常","1":"PRBS7","2":"PRBS15","3":"PRBS23","4":"全0","5":"全1","6":"交替01"}',NULL,NULL,1,4,280,'0','测试序列',NOW()),
('ENCODE','ENC_BYPASS','旁路模式','SWITCH',NULL,NULL,NULL,1,1,290,'0','编码旁路',NOW()),
('ENCODE','ENC_RESET','软复位','SWITCH',NULL,NULL,NULL,1,1,300,'0','模块复位',NOW());

-- ========================================
-- 二、调制单元 MODULATE - 35个参数
-- ========================================
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, enum_options, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
-- 基础调制参数 4个
('MODULATE','MOD_SCHEME','调制方式','ENUM','{"0":"BPSK","1":"QPSK","2":"OQPSK","3":"DQPSK","4":"8PSK","5":"16APSK","6":"32APSK","7":"16QAM","8":"64QAM","9":"256QAM","10":"GMSK","11":"GFSK"}',NULL,NULL,1,8,10,'1','调制体制',NOW()),
('MODULATE','MOD_SYMBOL_RATE','符号率(kSps)','FLOAT',NULL,1.000,100000.000,1000,32,20,'1000000','单位kSps乘1000',NOW()),
('MODULATE','MOD_SAMPLE_RATE','采样率(MHz)','FLOAT',NULL,1.000,500.000,1000,32,30,'100000','单位MHz乘1000',NOW()),
('MODULATE','MOD_SAMPLES_PER_SYM','每符号采样数','UINT',NULL,2,32,1,8,40,'4','过采样率',NOW()),
-- 成形滤波器参数 4个
('MODULATE','MOD_FILTER_TYPE','成形滤波器','ENUM','{"0":"矩形","1":"升余弦","2":"根升余弦","3":"高斯","4":"半正弦"}',NULL,NULL,1,4,50,'2','脉冲成形',NOW()),
('MODULATE','MOD_ROLL_OFF','滚降系数','ENUM','{"0":"0.15","1":"0.20","2":"0.25","3":"0.30","4":"0.35","5":"0.40","6":"0.50"}',NULL,NULL,1,4,60,'2','alpha值',NOW()),
('MODULATE','MOD_FILTER_SPAN','滤波器跨度','UINT',NULL,2,32,1,8,70,'8','符号跨度',NOW()),
('MODULATE','MOD_BT_PRODUCT','BT积','FLOAT',NULL,0.100,1.000,1000,16,80,'300','GMSK的BT',NOW()),
-- 载波参数 4个
('MODULATE','MOD_IF_FREQ','中频频率(MHz)','FLOAT',NULL,0.000,500.000,1000,32,90,'70000','单位MHz乘1000',NOW()),
('MODULATE','MOD_FREQ_OFFSET','频率偏移(kHz)','FLOAT',NULL,-1000.000,1000.000,1000,32,100,'0','频偏补偿kHz',NOW()),
('MODULATE','MOD_PHASE_OFFSET','相位偏移(度)','FLOAT',NULL,-180.000,180.000,100,16,110,'0','相位补偿',NOW()),
('MODULATE','MOD_CARRIER_SUPPRESS','载波抑制','SWITCH',NULL,NULL,NULL,1,1,120,'0','抑制载波',NOW()),
-- 功率控制 4个
('MODULATE','MOD_OUTPUT_POWER','输出功率(dBm)','FLOAT',NULL,-40.000,20.000,10,16,130,'0','发射功率',NOW()),
('MODULATE','MOD_POWER_BACKOFF','功率回退(dB)','FLOAT',NULL,0.000,20.000,10,16,140,'30','峰均比回退',NOW()),
('MODULATE','MOD_AGC_ENABLE','AGC使能','SWITCH',NULL,NULL,NULL,1,1,150,'1','自动增益',NOW()),
('MODULATE','MOD_AGC_TARGET','AGC目标(dBFS)','FLOAT',NULL,-30.000,0.000,10,16,160,'-100','目标电平',NOW()),
-- 预失真与补偿 5个
('MODULATE','MOD_PREDIST_ENABLE','预失真使能','SWITCH',NULL,NULL,NULL,1,1,170,'0','DPD开关',NOW()),
('MODULATE','MOD_IQ_GAIN_IMBAL','IQ增益不平衡','FLOAT',NULL,-3.000,3.000,1000,16,180,'0','dB乘1000',NOW()),
('MODULATE','MOD_IQ_PHASE_IMBAL','IQ相位不平衡','FLOAT',NULL,-10.000,10.000,100,16,190,'0','度乘100',NOW()),
('MODULATE','MOD_DC_OFFSET_I','I路直流偏置','FLOAT',NULL,-1.000,1.000,10000,16,200,'0','归一化',NOW()),
('MODULATE','MOD_DC_OFFSET_Q','Q路直流偏置','FLOAT',NULL,-1.000,1.000,10000,16,210,'0','归一化',NOW()),
-- 时钟与同步 3个
('MODULATE','MOD_CLOCK_SOURCE','时钟源','ENUM','{"0":"内部晶振","1":"外部10MHz","2":"外部VCXO","3":"GPSDO","4":"IEEE1588"}',NULL,NULL,1,4,220,'0','参考时钟',NOW()),
('MODULATE','MOD_SYNC_MODE','同步模式','ENUM','{"0":"自由运行","1":"外部PPS","2":"软件触发","3":"帧同步"}',NULL,NULL,1,4,230,'0','同步方式',NOW()),
('MODULATE','MOD_TIMING_ADV','定时提前量(us)','FLOAT',NULL,0.000,1000.000,1000,32,240,'0','微秒乘1000',NOW()),
-- 突发与帧控制 4个
('MODULATE','MOD_BURST_MODE','突发模式','SWITCH',NULL,NULL,NULL,1,1,250,'0','突发开关',NOW()),
('MODULATE','MOD_RAMP_UP_TIME','上升时间(us)','FLOAT',NULL,0.100,100.000,1000,16,260,'10000','微秒乘1000',NOW()),
('MODULATE','MOD_RAMP_DOWN_TIME','下降时间(us)','FLOAT',NULL,0.100,100.000,1000,16,270,'10000','微秒乘1000',NOW()),
('MODULATE','MOD_GUARD_TIME','保护时间(us)','FLOAT',NULL,0.000,1000.000,1000,16,280,'0','微秒乘1000',NOW()),
-- 频谱控制 2个
('MODULATE','MOD_SPECTRUM_INVERT','频谱反转','SWITCH',NULL,NULL,NULL,1,1,290,'0','镜像频谱',NOW()),
('MODULATE','MOD_SIDEBAND_SUPPRESS','边带抑制(dB)','FLOAT',NULL,0.000,80.000,10,16,300,'400','抑制度',NOW()),
-- 功放控制 2个
('MODULATE','MOD_PA_ENABLE','功放使能','SWITCH',NULL,NULL,NULL,1,1,310,'0','PA开关',NOW()),
('MODULATE','MOD_PA_BIAS','功放偏置','UINT',NULL,0,4095,1,16,320,'2048','DAC值',NOW()),
-- 测试与监控 3个
('MODULATE','MOD_TEST_TONE','测试音使能','SWITCH',NULL,NULL,NULL,1,1,330,'0','单音测试',NOW()),
('MODULATE','MOD_TEST_FREQ','测试音频率(kHz)','FLOAT',NULL,0.100,10000.000,1000,32,340,'1000000','kHz乘1000',NOW()),
('MODULATE','MOD_LOOPBACK','环回模式','ENUM','{"0":"关闭","1":"数字环回","2":"模拟环回","3":"射频环回"}',NULL,NULL,1,2,350,'0','测试环回',NOW());


-- ========================================
-- 三、解调单元 DEMODULATE - 40个参数
-- ========================================
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, enum_options, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
-- 基础解调参数 4个
('DEMODULATE','DEM_SCHEME','解调方式','ENUM','{"0":"BPSK","1":"QPSK","2":"OQPSK","3":"DQPSK","4":"8PSK","5":"16APSK","6":"32APSK","7":"16QAM","8":"64QAM","9":"256QAM","10":"GMSK","11":"GFSK","12":"自动识别"}',NULL,NULL,1,8,10,'12','解调体制',NOW()),
('DEMODULATE','DEM_SYMBOL_RATE','符号率(kSps)','FLOAT',NULL,1.000,100000.000,1000,32,20,'1000000','单位kSps乘1000',NOW()),
('DEMODULATE','DEM_SAMPLE_RATE','采样率(MHz)','FLOAT',NULL,1.000,500.000,1000,32,30,'100000','单位MHz乘1000',NOW()),
('DEMODULATE','DEM_SAMPLES_PER_SYM','每符号采样数','UINT',NULL,2,32,1,8,40,'4','过采样率',NOW()),
-- 匹配滤波器 3个
('DEMODULATE','DEM_FILTER_TYPE','匹配滤波器','ENUM','{"0":"矩形","1":"升余弦","2":"根升余弦","3":"高斯","4":"半正弦"}',NULL,NULL,1,4,50,'2','脉冲匹配',NOW()),
('DEMODULATE','DEM_ROLL_OFF','滚降系数','ENUM','{"0":"0.15","1":"0.20","2":"0.25","3":"0.30","4":"0.35","5":"0.40","6":"0.50"}',NULL,NULL,1,4,60,'2','alpha值',NOW()),
('DEMODULATE','DEM_FILTER_SPAN','滤波器跨度','UINT',NULL,2,32,1,8,70,'8','符号跨度',NOW()),
-- 输入信号控制 3个
('DEMODULATE','DEM_IF_FREQ','中频频率(MHz)','FLOAT',NULL,0.000,500.000,1000,32,80,'70000','单位MHz乘1000',NOW()),
('DEMODULATE','DEM_INPUT_BW','输入带宽(MHz)','FLOAT',NULL,0.010,200.000,1000,32,90,'20000','单位MHz乘1000',NOW()),
('DEMODULATE','DEM_INPUT_ATTEN','输入衰减(dB)','FLOAT',NULL,0.000,31.500,10,16,100,'0','0.1dB步进',NOW()),
-- AGC参数 5个
('DEMODULATE','DEM_AGC_ENABLE','AGC使能','SWITCH',NULL,NULL,NULL,1,1,110,'1','自动增益',NOW()),
('DEMODULATE','DEM_AGC_MODE','AGC模式','ENUM','{"0":"慢速","1":"中速","2":"快速","3":"自适应"}',NULL,NULL,1,2,120,'2','响应速度',NOW()),
('DEMODULATE','DEM_AGC_TARGET','AGC目标(dBFS)','FLOAT',NULL,-80.000,-10.000,100,16,130,'-4000','目标电平',NOW()),
('DEMODULATE','DEM_AGC_ATTACK','AGC攻击时间(ms)','FLOAT',NULL,0.100,100.000,1000,16,140,'10000','毫秒乘1000',NOW()),
('DEMODULATE','DEM_AGC_DECAY','AGC衰减时间(ms)','FLOAT',NULL,1.000,1000.000,1000,16,150,'100000','毫秒乘1000',NOW()),
-- 载波恢复 6个
('DEMODULATE','DEM_CARRIER_LOOP_TYPE','载波环类型','ENUM','{"0":"科斯塔斯","1":"判决反馈","2":"盲估计","3":"导频辅助"}',NULL,NULL,1,2,160,'0','载波恢复',NOW()),
('DEMODULATE','DEM_CARRIER_LOOP_BW','载波环带宽(Hz)','FLOAT',NULL,0.100,10000.000,100,32,170,'10000','Hz乘100',NOW()),
('DEMODULATE','DEM_CARRIER_LOOP_DAMP','载波环阻尼','FLOAT',NULL,0.100,2.000,1000,16,180,'707','阻尼系数',NOW()),
('DEMODULATE','DEM_FREQ_OFFSET_MAX','最大频偏(kHz)','FLOAT',NULL,0.100,1000.000,1000,32,190,'100000','kHz乘1000',NOW()),
('DEMODULATE','DEM_FREQ_SEARCH_ENABLE','频率搜索','SWITCH',NULL,NULL,NULL,1,1,200,'1','自动搜频',NOW()),
('DEMODULATE','DEM_FREQ_SEARCH_STEP','搜索步进(Hz)','FLOAT',NULL,10.000,10000.000,100,32,210,'100000','Hz乘100',NOW()),
-- 定时恢复 4个
('DEMODULATE','DEM_TIMING_LOOP_TYPE','定时环类型','ENUM','{"0":"Gardner","1":"MM","2":"早迟门","3":"平方环"}',NULL,NULL,1,2,220,'0','符号同步',NOW()),
('DEMODULATE','DEM_TIMING_LOOP_BW','定时环带宽(Hz)','FLOAT',NULL,0.010,5000.000,1000,32,230,'50000','Hz乘1000',NOW()),
('DEMODULATE','DEM_TIMING_LOOP_DAMP','定时环阻尼','FLOAT',NULL,0.100,2.000,1000,16,240,'707','阻尼系数',NOW()),
('DEMODULATE','DEM_TIMING_ERROR_MAX','最大定时误差','FLOAT',NULL,0.010,0.500,10000,16,250,'2500','符号周期',NOW()),
-- IQ校准 4个
('DEMODULATE','DEM_IQ_CAL_ENABLE','IQ校准使能','SWITCH',NULL,NULL,NULL,1,1,260,'1','自动校准',NOW()),
('DEMODULATE','DEM_IQ_GAIN_COMP','IQ增益补偿','FLOAT',NULL,-3.000,3.000,1000,16,270,'0','dB乘1000',NOW()),
('DEMODULATE','DEM_IQ_PHASE_COMP','IQ相位补偿','FLOAT',NULL,-10.000,10.000,100,16,280,'0','度乘100',NOW()),
('DEMODULATE','DEM_DC_REMOVE','直流消除','SWITCH',NULL,NULL,NULL,1,1,290,'1','DC阻断',NOW()),
-- 均衡器 4个
('DEMODULATE','DEM_EQUALIZER_ENABLE','均衡器使能','SWITCH',NULL,NULL,NULL,1,1,300,'1','自适应均衡',NOW()),
('DEMODULATE','DEM_EQUALIZER_TYPE','均衡器类型','ENUM','{"0":"LMS","1":"RLS","2":"CMA","3":"判决反馈"}',NULL,NULL,1,2,310,'0','均衡算法',NOW()),
('DEMODULATE','DEM_EQUALIZER_TAPS','均衡器抽头数','UINT',NULL,3,63,1,8,320,'15','滤波器阶数',NOW()),
('DEMODULATE','DEM_EQUALIZER_STEP','均衡器步长','FLOAT',NULL,0.0001,0.1000,100000,16,330,'100','收敛速度',NOW()),
-- 信号质量监测 4个
('DEMODULATE','DEM_SNR_ESTIMATE','SNR估计使能','SWITCH',NULL,NULL,NULL,1,1,340,'1','信噪比测量',NOW()),
('DEMODULATE','DEM_CNO_THRESHOLD','CNO门限(dB-Hz)','FLOAT',NULL,10.000,80.000,10,16,350,'350','载噪比告警',NOW()),
('DEMODULATE','DEM_EVM_THRESHOLD','EVM门限(%)','FLOAT',NULL,1.000,50.000,10,16,360,'100','误差矢量',NOW()),
('DEMODULATE','DEM_BER_THRESHOLD','BER门限','FLOAT',NULL,0.000001,0.100000,1000000,32,370,'10000','误码率',NOW()),
-- 锁定与告警 4个
('DEMODULATE','DEM_LOCK_DETECT_ENABLE','锁定检测','SWITCH',NULL,NULL,NULL,1,1,380,'1','锁定指示',NOW()),
('DEMODULATE','DEM_LOCK_THRESHOLD','锁定门限','FLOAT',NULL,0.100,1.000,1000,16,390,'800','锁定判决',NOW()),
('DEMODULATE','DEM_LOCK_TIMEOUT','锁定超时(ms)','UINT',NULL,10,10000,1,16,400,'500','失锁超时',NOW()),
('DEMODULATE','DEM_REACQ_ENABLE','重捕获使能','SWITCH',NULL,NULL,NULL,1,1,410,'1','自动重捕',NOW()),
-- 频谱与极性 3个
('DEMODULATE','DEM_SPECTRUM_INVERT','频谱反转','SWITCH',NULL,NULL,NULL,1,1,420,'0','镜像频谱',NOW()),
('DEMODULATE','DEM_SYMBOL_INVERT','符号反转','SWITCH',NULL,NULL,NULL,1,1,430,'0','比特反转',NOW()),
('DEMODULATE','DEM_DIFF_DECODE','差分解码','SWITCH',NULL,NULL,NULL,1,1,440,'0','差分模式',NOW()),
-- 测试与调试 2个
('DEMODULATE','DEM_LOOPBACK','环回模式','ENUM','{"0":"关闭","1":"数字环回","2":"模拟环回"}',NULL,NULL,1,2,450,'0','测试环回',NOW()),
('DEMODULATE','DEM_FREEZE','冻结环路','SWITCH',NULL,NULL,NULL,1,1,460,'0','环路保持',NOW());

-- ========================================
-- 四、译码单元 DECODE - 32个参数
-- ========================================
INSERT INTO sys_baseband_param_def
(unit_type, param_code, param_name, value_type, enum_options, min_value, max_value, scale_factor, bit_length, hardware_order, default_value, remark, create_time)
VALUES
-- 基础译码参数 4个
('DECODE','DEC_FEC_TYPE','译码类型','ENUM','{"0":"无译码","1":"Viterbi","2":"Turbo","3":"LDPC","4":"RS","5":"BCH","6":"极化码","7":"级联码"}',NULL,NULL,1,4,10,'1','FEC译码',NOW()),
('DECODE','DEC_CODE_RATE','译码码率','ENUM','{"0":"1/2","1":"1/3","2":"1/4","3":"2/3","4":"3/4","5":"4/5","6":"5/6","7":"7/8","8":"8/9"}',NULL,NULL,1,4,20,'0','码率匹配',NOW()),
('DECODE','DEC_CONSTRAINT_LEN','约束长度','UINT',NULL,3,15,1,8,30,'7','卷积码K值',NOW()),
('DECODE','DEC_TRACEBACK_DEPTH','回溯深度','UINT',NULL,16,512,1,16,40,'64','Viterbi深度',NOW()),
-- 软判决参数 3个
('DECODE','DEC_SOFT_DECISION','软判决使能','SWITCH',NULL,NULL,NULL,1,1,50,'1','软信息',NOW()),
('DECODE','DEC_SOFT_WIDTH','软判决位宽','UINT',NULL,2,10,1,4,60,'6','量化位数',NOW()),
('DECODE','DEC_METRIC_TYPE','度量类型','ENUM','{"0":"欧氏距离","1":"汉明距离","2":"对数似然比"}',NULL,NULL,1,2,70,'2','路径度量',NOW()),
-- 迭代译码参数 4个
('DECODE','DEC_ITER_MAX','最大迭代次数','UINT',NULL,1,50,1,8,80,'8','Turbo LDPC',NOW()),
('DECODE','DEC_ITER_MIN','最小迭代次数','UINT',NULL,1,20,1,8,90,'4','最少迭代',NOW()),
('DECODE','DEC_EARLY_STOP','早停使能','SWITCH',NULL,NULL,NULL,1,1,100,'1','提前终止',NOW()),
('DECODE','DEC_EARLY_STOP_THR','早停门限','FLOAT',NULL,0.001,1.000,10000,16,110,'100','收敛判决',NOW()),
-- 去交织参数 4个
('DECODE','DEC_DEINTERLEAVER_TYPE','去交织类型','ENUM','{"0":"不去交织","1":"块去交织","2":"卷积去交织","3":"随机去交织","4":"S随机去交织"}',NULL,NULL,1,4,120,'1','去交织方式',NOW()),
('DECODE','DEC_DEINTERLEAVER_ROWS','去交织行数','UINT',NULL,1,256,1,16,130,'16','块去交织行',NOW()),
('DECODE','DEC_DEINTERLEAVER_COLS','去交织列数','UINT',NULL,1,256,1,16,140,'32','块去交织列',NOW()),
('DECODE','DEC_DEINTERLEAVER_DEPTH','去交织深度','UINT',NULL,1,1024,1,16,150,'64','卷积深度',NOW()),
-- 帧同步参数 5个
('DECODE','DEC_FRAME_SYNC_ENABLE','帧同步使能','SWITCH',NULL,NULL,NULL,1,1,160,'1','帧同步',NOW()),
('DECODE','DEC_FRAME_SYNC_WORD','帧同步字','UINT',NULL,0,4294967295,1,32,170,'429496729','32bit同步码',NOW()),
('DECODE','DEC_FRAME_SYNC_TOL','同步容错(bit)','UINT',NULL,0,16,1,5,180,'2','允许错位',NOW()),
('DECODE','DEC_FRAME_LENGTH','帧长(bit)','UINT',NULL,64,65536,1,32,190,'1024','信息位长度',NOW()),
('DECODE','DEC_FRAME_HEADER_LEN','帧头长度(byte)','UINT',NULL,0,256,1,16,200,'16','帧头字节数',NOW()),
-- CRC校验参数 4个
('DECODE','DEC_CRC_CHECK','CRC校验使能','SWITCH',NULL,NULL,NULL,1,1,210,'1','CRC检查',NOW()),
('DECODE','DEC_CRC_TYPE','CRC类型','ENUM','{"0":"无CRC","1":"CRC8","2":"CRC16-CCITT","3":"CRC16-IBM","4":"CRC24","5":"CRC32"}',NULL,NULL,1,4,220,'2','校验类型',NOW()),
('DECODE','DEC_CRC_POLY','CRC多项式','UINT',NULL,0,4294967295,1,32,230,'4129','CRC生成多项式',NOW()),
('DECODE','DEC_CRC_ERROR_ACTION','CRC错误处理','ENUM','{"0":"丢弃","1":"标记","2":"输出"}',NULL,NULL,1,2,240,'1','错误策略',NOW()),
-- 解扰参数 3个
('DECODE','DEC_DESCRAMBLE_ENABLE','解扰使能','SWITCH',NULL,NULL,NULL,1,1,250,'1','解扰码',NOW()),
('DECODE','DEC_DESCRAMBLE_POLY','解扰多项式','ENUM','{"0":"x7+x1+1","1":"x9+x5+1","2":"x11+x9+1","3":"x15+x14+1","4":"x17+x14+1","5":"x23+x18+1"}',NULL,NULL,1,4,260,'1','解扰生成多项式',NOW()),
('DECODE','DEC_DESCRAMBLE_INIT','解扰初始值','UINT',NULL,0,16777215,1,32,270,'1','解扰寄存器初值',NOW()),
-- 输出控制 3个
('DECODE','DEC_OUTPUT_FORMAT','输出格式','ENUM','{"0":"裸数据","1":"含帧头","2":"含时间戳","3":"含质量信息"}',NULL,NULL,1,2,280,'1','输出格式',NOW()),
('DECODE','DEC_OUTPUT_INVERT','输出反转','SWITCH',NULL,NULL,NULL,1,1,290,'0','比特反转',NOW()),
('DECODE','DEC_DATA_ENDIAN','数据字节序','ENUM','{"0":"大端","1":"小端"}',NULL,NULL,1,1,300,'0','字节序',NOW()),
-- 误码统计 3个
('DECODE','DEC_BER_MEASURE','误码测量使能','SWITCH',NULL,NULL,NULL,1,1,310,'1','BER统计',NOW()),
('DECODE','DEC_BER_WINDOW','误码统计窗口','UINT',NULL,10,100000,1,32,320,'10000','统计帧数',NOW()),
('DECODE','DEC_FER_MEASURE','误帧测量使能','SWITCH',NULL,NULL,NULL,1,1,330,'1','FER统计',NOW()),
-- 测试与调试 3个
('DECODE','DEC_BYPASS','旁路模式','ENUM','{"0":"正常译码","1":"译码前旁路","2":"译码后旁路","3":"完全旁路"}',NULL,NULL,1,2,340,'0','调试模式',NOW()),
('DECODE','DEC_TEST_PATTERN','测试序列','ENUM','{"0":"关闭","1":"PRBS7","2":"PRBS15","3":"PRBS23"}',NULL,NULL,1,2,350,'0','测试模式',NOW()),
('DECODE','DEC_RESET','软复位','SWITCH',NULL,NULL,NULL,1,1,360,'0','模块复位',NOW());

-- ========================================
-- 查询统计信息
-- ========================================
SELECT 
    unit_type AS '单元类型',
    COUNT(*) AS '参数数量'
FROM sys_baseband_param_def
GROUP BY unit_type
ORDER BY unit_type;

SELECT 
    value_type AS '值类型',
    COUNT(*) AS '数量',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM sys_baseband_param_def), 1), '%') AS '占比'
FROM sys_baseband_param_def
GROUP BY value_type
ORDER BY COUNT(*) DESC;

SELECT COUNT(*) AS '参数总数' FROM sys_baseband_param_def;
