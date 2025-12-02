-- 快速添加模式类型字段
USE ry-vue;
ALTER TABLE sys_baseband_param_constraint 
ADD COLUMN mode_types VARCHAR(200) DEFAULT NULL 
COMMENT '适用模式类型（多个用逗号分隔，如：KSA,KMA，空表示适用所有模式）' 
AFTER target_param_name;

SELECT '模式类型字段添加完成！' AS result;
