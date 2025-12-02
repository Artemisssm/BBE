-- 添加参数约束管理菜单
-- 创建日期：2024-12-02
-- 说明：在基带监控模块下添加参数约束管理菜单

-- ========================================
-- 1. 查找基带监控主菜单ID
-- ========================================
SELECT @baseband_menu_id := menu_id FROM sys_menu WHERE menu_name = '基带监控' AND parent_id = 1 LIMIT 1;

-- 如果找不到基带监控菜单，显示错误
SELECT IF(@baseband_menu_id IS NULL, '错误：未找到基带监控主菜单，请先执行 baseband_menu.sql', '找到基带监控菜单ID') AS status;

-- ========================================
-- 2. 添加参数约束管理菜单
-- ========================================
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component, 
    is_frame, is_cache, menu_type, visible, status, 
    perms, icon, create_by, create_time, remark
) VALUES (
    '参数约束', @baseband_menu_id, '3', 'constraint', 'system/baseband/paramConstraint/index',
    1, 0, 'C', '0', '0',
    'system:paramConstraint:list', 'link', 'admin', NOW(), '参数约束管理菜单'
);

-- 获取参数约束菜单ID
SELECT @constraint_menu_id := LAST_INSERT_ID();

-- ========================================
-- 3. 添加参数约束管理按钮
-- ========================================

-- 查询按钮
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status,
    perms, icon, create_by, create_time, remark
) VALUES (
    '约束查询', @constraint_menu_id, '1', '#', '',
    1, 0, 'F', '0', '0',
    'system:paramConstraint:query', '#', 'admin', NOW(), ''
);

-- 新增按钮
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status,
    perms, icon, create_by, create_time, remark
) VALUES (
    '约束新增', @constraint_menu_id, '2', '#', '',
    1, 0, 'F', '0', '0',
    'system:paramConstraint:add', '#', 'admin', NOW(), ''
);

-- 修改按钮
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status,
    perms, icon, create_by, create_time, remark
) VALUES (
    '约束修改', @constraint_menu_id, '3', '#', '',
    1, 0, 'F', '0', '0',
    'system:paramConstraint:edit', '#', 'admin', NOW(), ''
);

-- 删除按钮
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status,
    perms, icon, create_by, create_time, remark
) VALUES (
    '约束删除', @constraint_menu_id, '4', '#', '',
    1, 0, 'F', '0', '0',
    'system:paramConstraint:remove', '#', 'admin', NOW(), ''
);

-- 导出按钮
INSERT INTO sys_menu (
    menu_name, parent_id, order_num, path, component,
    is_frame, is_cache, menu_type, visible, status,
    perms, icon, create_by, create_time, remark
) VALUES (
    '约束导出', @constraint_menu_id, '5', '#', '',
    1, 0, 'F', '0', '0',
    'system:paramConstraint:export', '#', 'admin', NOW(), ''
);

-- ========================================
-- 4. 验证菜单添加结果
-- ========================================
SELECT 
    m1.menu_id AS '主菜单ID',
    m1.menu_name AS '主菜单名称',
    m2.menu_id AS '子菜单ID',
    m2.menu_name AS '子菜单名称',
    m2.path AS '路由路径',
    m2.component AS '组件路径',
    m2.perms AS '权限标识',
    m2.menu_type AS '菜单类型'
FROM sys_menu m1
LEFT JOIN sys_menu m2 ON m1.menu_id = m2.parent_id
WHERE m1.menu_name = '基带监控'
ORDER BY m1.order_num, m2.order_num;

-- ========================================
-- 5. 显示完成信息
-- ========================================
SELECT '参数约束管理菜单添加完成！' AS '状态',
       '请刷新浏览器，在基带监控菜单下可以看到"参数约束"选项' AS '说明';
