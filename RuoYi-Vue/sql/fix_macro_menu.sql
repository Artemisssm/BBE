-- ========================================
-- 修复宏管理菜单重复问题
-- ========================================

-- 1. 查看当前宏管理菜单
SELECT '========================================' AS '';
SELECT '当前宏管理菜单：' AS '';
SELECT '========================================' AS '';

SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    path AS '路由',
    component AS '组件',
    create_time AS '创建时间'
FROM sys_menu 
WHERE menu_name = '宏管理'
ORDER BY menu_id;

-- 2. 删除所有宏管理相关菜单（包括子菜单）
SELECT '========================================' AS '';
SELECT '正在删除重复的宏管理菜单...' AS '';
SELECT '========================================' AS '';

-- 先删除宏管理的子菜单（权限按钮）
DELETE FROM sys_menu 
WHERE parent_id IN (
    SELECT menu_id FROM (
        SELECT menu_id FROM sys_menu WHERE menu_name = '宏管理'
    ) AS temp
);

-- 再删除宏管理主菜单
DELETE FROM sys_menu WHERE menu_name = '宏管理';

-- 3. 重新添加宏管理菜单
SELECT '========================================' AS '';
SELECT '重新添加宏管理菜单...' AS '';
SELECT '========================================' AS '';

-- 查找基带监控主菜单ID
SELECT @baseband_menu_id := menu_id FROM sys_menu WHERE menu_name = '基带监控' AND parent_id = 1 LIMIT 1;

-- 添加宏管理主菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏管理', @baseband_menu_id, '3', 'macro', 'system/baseband/macro/index', 1, 0, 'C', '0', '0', 'system:basebandMacro:list', 'skill', 'admin', NOW(), '', null, '宏管理菜单');

-- 获取宏管理菜单ID
SELECT @macro_menu_id := LAST_INSERT_ID();

-- 添加权限按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
('宏查询', @macro_menu_id, '1', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:query', '#', 'admin', NOW(), '', null, ''),
('宏新增', @macro_menu_id, '2', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:add', '#', 'admin', NOW(), '', null, ''),
('宏修改', @macro_menu_id, '3', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:edit', '#', 'admin', NOW(), '', null, ''),
('宏删除', @macro_menu_id, '4', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:remove', '#', 'admin', NOW(), '', null, ''),
('宏导出', @macro_menu_id, '5', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:export', '#', 'admin', NOW(), '', null, ''),
('参数配置', @macro_menu_id, '6', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:config', '#', 'admin', NOW(), '', null, '');

-- 4. 验证结果
SELECT '========================================' AS '';
SELECT '修复完成！当前菜单结构：' AS '';
SELECT '========================================' AS '';

SELECT 
    m1.menu_id AS '主菜单ID',
    m1.menu_name AS '主菜单',
    m2.menu_id AS '子菜单ID',
    m2.menu_name AS '子菜单',
    m2.order_num AS '排序',
    m2.path AS '路由',
    m2.component AS '组件路径',
    m2.perms AS '权限标识'
FROM sys_menu m1
LEFT JOIN sys_menu m2 ON m1.menu_id = m2.parent_id
WHERE m1.menu_name = '基带监控'
ORDER BY m2.order_num;

SELECT '========================================' AS '';
SELECT '请执行以下操作：' AS '';
SELECT '1. 刷新浏览器（Ctrl+F5）' AS '';
SELECT '2. 重新登录系统' AS '';
SELECT '3. 检查宏管理菜单是否正常' AS '';
SELECT '========================================' AS '';
