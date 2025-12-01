-- 为 sys_baseband_unit 表添加模式类型字段
ALTER TABLE sys_baseband_unit ADD COLUMN mode_type VARCHAR(20) DEFAULT NULL COMMENT '模式类型：KSA/KMA/SSA/基带数传';
