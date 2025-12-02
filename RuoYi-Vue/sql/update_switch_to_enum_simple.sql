-- 将参数定义中的SWITCH类型改为ENUM类型（简化版）
-- 适用于MySQL 5.7及以上版本

-- 1. 查看当前有哪些SWITCH类型的参数
SELECT 
    param_id,
    param_code,
    param_name,
    unit_type,
    value_type,
    min_value,
    max_value,
    enum_options,
    '待更新' AS status
FROM sys_baseband_param_def
WHERE value_type = 'SWITCH';

-- 2. 更新SWITCH类型为ENUM类型
-- 注意：需要手动为每个参数设置枚举选项
UPDATE sys_baseband_param_def
SET 
    value_type = 'ENUM',
    enum_options = '{"0": "关", "1": "开"}'
WHERE value_type = 'SWITCH'
  AND (enum_options IS NULL OR enum_options = '');

-- 3. 如果有特殊的开关参数（不是0/1），需要单独更新
-- 示例：如果某个参数的值是false/true
-- UPDATE sys_baseband_param_def
-- SET enum_options = '{"false": "关", "true": "开"}'
-- WHERE param_code = 'YOUR_PARAM_CODE';

-- 4. 验证更新结果
SELECT 
    param_id,
    param_code,
    param_name,
    unit_type,
    value_type,
    enum_options,
    '已更新' AS status
FROM sys_baseband_param_def
WHERE value_type = 'ENUM' 
  AND (enum_options LIKE '%关%' OR enum_options LIKE '%开%');

-- 5. 检查是否还有SWITCH类型
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✓ 所有SWITCH类型已更新为ENUM'
        ELSE CONCAT('✗ 还有 ', COUNT(*), ' 个SWITCH类型未更新')
    END AS '更新状态'
FROM sys_baseband_param_def
WHERE value_type = 'SWITCH';

-- 使用说明：
-- 1. 此SQL将所有SWITCH类型改为ENUM类型
-- 2. 默认枚举选项：{"0": "关", "1": "开"}
-- 3. 如果你的开关参数使用其他值，请修改enum_options
-- 4. 已有的参数值不受影响
-- 5. 前端界面会自动使用下拉选择器代替开关控件
