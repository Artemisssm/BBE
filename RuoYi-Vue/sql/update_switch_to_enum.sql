-- 将参数定义中的SWITCH类型改为ENUM类型
-- 并为开关类型的参数添加枚举选项

-- 1. 查看当前有哪些SWITCH类型的参数
SELECT 
    param_id,
    param_code,
    param_name,
    unit_type,
    value_type,
    min_value,
    max_value,
    enum_options
FROM sys_baseband_param_def
WHERE value_type = 'SWITCH';

-- 2. 更新SWITCH类型为ENUM类型，并添加枚举选项
UPDATE sys_baseband_param_def
SET 
    value_type = 'ENUM',
    enum_options = CASE
        -- 如果已经有枚举选项，保持不变
        WHEN enum_options IS NOT NULL AND enum_options != '' THEN enum_options
        -- 否则根据min_value和max_value生成枚举选项
        ELSE JSON_OBJECT(
            COALESCE(min_value, '0'), '关',
            COALESCE(max_value, '1'), '开'
        )
    END
WHERE value_type = 'SWITCH';

-- 3. 验证更新结果
SELECT 
    param_id,
    param_code,
    param_name,
    unit_type,
    value_type,
    enum_options,
    '更新成功' AS status
FROM sys_baseband_param_def
WHERE param_code IN (
    SELECT param_code 
    FROM sys_baseband_param_def 
    WHERE value_type = 'ENUM' 
    AND enum_options LIKE '%关%'
);

-- 4. 显示完成信息
SELECT 
    COUNT(*) AS '更新数量',
    'SWITCH类型已全部改为ENUM类型' AS '状态'
FROM sys_baseband_param_def
WHERE value_type = 'ENUM' 
AND enum_options LIKE '%关%';

-- 注意事项：
-- 1. 此SQL会将所有SWITCH类型改为ENUM类型
-- 2. 枚举选项格式：{"0": "关", "1": "开"}
-- 3. 如果min_value和max_value不是0和1，会使用实际值
-- 4. 已有的参数值不受影响，因为值本身没有变化（仍然是0或1）
