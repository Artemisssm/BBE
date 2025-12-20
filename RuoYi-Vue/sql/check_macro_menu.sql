-- ========================================
-- 宏管理菜单检查脚本
-- ========================================

SELECT '========================================' AS '';
SELECT '宏管理菜单检查报告' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';

-- 1. 检查宏管理主菜单数量
SELECT '1. 宏管理主菜单数量检查' AS '';
SELECT '---' AS '';
SELECT 
    COUNT(*) AS '数量',
    CASE 
        WHEN COUNT(*) = 0 THEN '❌ 错误：菜单不存在'
        WHEN COUNT(*) = 1 THEN '✅ 正常'
        ELSE '❌ 错误：菜单重复'
    END AS '状态'
FROM sys_menu 
WHERE menu_name = '宏管理' AND menu_type = 'C';

SELECT '' AS '';

-- 2. 检查宏管理菜单详情
SELECT '2. 宏管理菜单配置检查' AS '';
SELECT '---' AS '';
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    path AS '路由路径',
    component AS '组件路径',
    perms AS '权限标识',
    CASE 
        WHEN path = 'macro' AND component = 'system/baseband/macro/index' THEN '✅ 配置正确'
        ELSE '❌ 配置错误'
    END AS '状态'
FROM sys_menu 
WHERE menu_name = '宏管理' AND menu_type = 'C';

SELECT '' AS '';

-- 3. 检查权限按钮数量
SELECT '3. 宏管理权限按钮检查' AS '';
SELECT '---' AS '';
SELECT 
    COUNT(*) AS '数量',
    CASE 
        WHEN COUNT(*) = 6 THEN '✅ 正常（应该有6个）'
        WHEN COUNT(*) = 0 THEN '❌ 错误：权限按钮不存在'
        ELSE CONCAT('⚠️  警告：数量不对（应该是6个，实际', COUNT(*), '个）')
    END AS '状态'
FROM sys_menu 
WHERE parent_id IN (
    SELECT menu_id FROM sys_menu WHERE menu_name = '宏管理' AND menu_type = 'C'
);

SELECT '' AS '';

-- 4. 列出所有权限按钮
SELECT '4. 权限按钮列表' AS '';
SELECT '---' AS '';
SELECT 
    menu_name AS '按钮名称',
    perms AS '权限标识',
    order_num AS '排序'
FROM sys_menu 
WHERE parent_id IN (
    SELECT menu_id FROM sys_menu WHERE menu_name = '宏管理' AND menu_type = 'C'
)
ORDER BY order_num;

SELECT '' AS '';

-- 5. 检查基带监控菜单结构
SELECT '5. 基带监控菜单结构' AS '';
SELECT '---' AS '';
SELECT 
    m2.order_num AS '排序',
    m2.menu_name AS '子菜单',
    m2.path AS '路由',
    m2.component AS '组件',
    CASE 
        WHEN m2.menu_name = '宏管理' AND m2.path = 'macro' THEN '✅'
        WHEN m2.menu_name = '宏管理' THEN '❌'
        ELSE ''
    END AS '状态'
FROM sys_menu m1
LEFT JOIN sys_menu m2 ON m1.menu_id = m2.parent_id
WHERE m1.menu_name = '基带监控' AND m2.menu_type = 'C'
ORDER BY m2.order_num;

SELECT '' AS '';

-- 6. 检查宏定义表
SELECT '6. 宏定义表检查' AS '';
SELECT '---' AS '';
SELECT 
    COUNT(*) AS '宏数量',
    CASE 
        WHEN COUNT(*) > 0 THEN CONCAT('✅ 有', COUNT(*), '个宏')
        ELSE '⚠️  表为空（这是正常的，可以手动创建宏）'
    END AS '状态'
FROM sys_baseband_macro;

SELECT '' AS '';

-- 7. 总结
SELECT '========================================' AS '';
SELECT '检查完成！' AS '';
SELECT '========================================' AS '';
SELECT '' AS '';
SELECT '如果发现问题，请执行以下操作：' AS '';
SELECT '1. 运行 fix_macro_menu.bat 修复菜单' AS '';
SELECT '2. 清除浏览器缓存（Ctrl+Shift+Delete）' AS '';
SELECT '3. 重新登录系统' AS '';
SELECT '4. 查看详细排查指南：宏管理问题排查和修复指南.md' AS '';
SELECT '========================================' AS '';
