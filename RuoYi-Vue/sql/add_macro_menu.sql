-- ========================================
-- 宏管理菜单 SQL 脚本
-- 创建日期：2025-12-09
-- 说明：在基带监控模块下添加宏管理菜单
-- ========================================

-- 1. 查找基带监控主菜单ID
SELECT @baseband_menu_id := menu_id FROM sys_menu WHERE menu_name = '基带监控' AND parent_id = 1 LIMIT 1;

-- 如果找不到基带监控菜单，显示错误
SELECT IF(@baseband_menu_id IS NULL, '错误：未找到基带监控主菜单，请先执行 baseband_menu.sql', '找到基带监控菜单ID') AS status;

-- 2. 添加宏管理菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏管理', @baseband_menu_id, '3', 'macro', 'system/baseband/macro/index', 1, 0, 'C', '0', '0', 'system:basebandMacro:list', 'skill', 'admin', sysdate(), '', null, '宏管理菜单');

-- 获取宏管理菜单ID
SELECT @macro_menu_id := LAST_INSERT_ID();

-- 3. 添加宏管理权限按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏查询', @macro_menu_id, '1', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:query', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏新增', @macro_menu_id, '2', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:add', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏修改', @macro_menu_id, '3', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:edit', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏删除', @macro_menu_id, '4', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:remove', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('宏导出', @macro_menu_id, '5', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:export', '#', 'admin', sysdate(), '', null, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES('参数配置', @macro_menu_id, '6', '#', '', 1, 0, 'F', '0', '0', 'system:basebandMacro:config', '#', 'admin', sysdate(), '', null, '');

-- 4. 显示菜单结构
SELECT '========================================' AS '';
SELECT '宏管理菜单添加完成！' AS '状态';
SELECT '========================================' AS '';

SELECT 
    m1.menu_id AS '主菜单ID',
    m1.menu_name AS '主菜单',
    m2.menu_id AS '子菜单ID',
    m2.menu_name AS '子菜单',
    m2.order_num AS '排序',
    m2.path AS '路由',
    m2.component AS '组件路径'
FROM sys_menu m1
LEFT JOIN sys_menu m2 ON m1.menu_id = m2.parent_id
WHERE m1.menu_name = '基带监控'
ORDER BY m1.order_num, m2.order_num;

-- 5. 完成提示
SELECT '========================================' AS '';
SELECT '请刷新浏览器，在基带监控菜单下可以看到"宏管理"选项' AS '说明';
SELECT '========================================' AS '';
