-- 基带监控模块菜单 SQL 脚本
-- 创建日期：2025-01-XX
-- 说明：包含基带监控主菜单、基带单元管理、参数定义管理的菜单和权限按钮

-- ========================================
-- 1. 基带监控主菜单（放在系统管理下）
-- ========================================
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带监控', '1', '6', 'baseband', NULL, 1, 0, 'M', '0', '0', NULL, 'monitor', 'admin', sysdate(), '', null, '基带监控管理目录');

-- 获取基带监控主菜单ID
SELECT @baseband_menu_id := LAST_INSERT_ID();

-- ========================================
-- 2. 基带单元管理菜单
-- ========================================
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带单元', @baseband_menu_id, '1', 'unit', 'system/baseband/unit/index', 1, 0, 'C', '0', '0', 'system:basebandUnit:list', 'table', 'admin', sysdate(), '', null, '基带单元菜单');

-- 获取基带单元菜单ID
SELECT @baseband_unit_menu_id := LAST_INSERT_ID();

-- 基带单元管理按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带单元查询', @baseband_unit_menu_id, '1', '#', '', 1, 0, 'F', '0', '0', 'system:basebandUnit:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带单元新增', @baseband_unit_menu_id, '2', '#', '', 1, 0, 'F', '0', '0', 'system:basebandUnit:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带单元修改', @baseband_unit_menu_id, '3', '#', '', 1, 0, 'F', '0', '0', 'system:basebandUnit:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带单元删除', @baseband_unit_menu_id, '4', '#', '', 1, 0, 'F', '0', '0', 'system:basebandUnit:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('基带单元导出', @baseband_unit_menu_id, '5', '#', '', 1, 0, 'F', '0', '0', 'system:basebandUnit:export', '#', 'admin', sysdate(), '', null, '');

-- ========================================
-- 3. 基带参数定义管理菜单
-- ========================================
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数定义', @baseband_menu_id, '2', 'param', 'system/baseband/paramDef/index', 1, 0, 'C', '0', '0', 'system:basebandParam:list', 'edit', 'admin', sysdate(), '', null, '基带参数定义菜单');

-- 获取参数定义菜单ID
SELECT @baseband_param_menu_id := LAST_INSERT_ID();

-- 参数定义管理按钮
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数定义查询', @baseband_param_menu_id, '1', '#', '', 1, 0, 'F', '0', '0', 'system:basebandParam:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数定义新增', @baseband_param_menu_id, '2', '#', '', 1, 0, 'F', '0', '0', 'system:basebandParam:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数定义修改', @baseband_param_menu_id, '3', '#', '', 1, 0, 'F', '0', '0', 'system:basebandParam:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数定义删除', @baseband_param_menu_id, '4', '#', '', 1, 0, 'F', '0', '0', 'system:basebandParam:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数定义导出', @baseband_param_menu_id, '5', '#', '', 1, 0, 'F', '0', '0', 'system:basebandParam:export', '#', 'admin', sysdate(), '', null, '');

-- ========================================
-- 4. 参数配置按钮（隐藏菜单，通过路由访问）
-- 注意：参数配置页面是隐藏的，不需要在菜单中显示
-- 但需要添加权限按钮，用于权限控制
-- ========================================
-- 参数配置权限（添加到基带单元菜单下）
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数配置查看', @baseband_unit_menu_id, '6', '#', '', 1, 0, 'F', '0', '0', 'system:basebandValue:list', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数配置保存', @baseband_unit_menu_id, '7', '#', '', 1, 0, 'F', '0', '0', 'system:basebandValue:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('参数配置下发', @baseband_unit_menu_id, '8', '#', '', 1, 0, 'F', '0', '0', 'system:basebandValue:dispatch', '#', 'admin', sysdate(), '', null, '');
