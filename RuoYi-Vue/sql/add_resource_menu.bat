@echo off
chcp 65001 >nul
echo ========================================
echo 板卡资源监控菜单添加工具
echo ========================================
echo.

set /p db_host="请输入数据库地址 (默认: localhost): "
if "%db_host%"=="" set db_host=localhost

set /p db_port="请输入数据库端口 (默认: 3306): "
if "%db_port%"=="" set db_port=3306

set /p db_name="请输入数据库名称 (默认: ry-vue): "
if "%db_name%"=="" set db_name=bbe

set /p db_user="请输入数据库用户名 (默认: root): "
if "%db_user%"=="" set db_user=root

set /p db_pass="请输入数据库密码: "

echo.
echo 正在添加板卡资源监控菜单...
echo.

mysql -h%db_host% -P%db_port% -u%db_user% -p%db_pass% %db_name% < add_resource_menu.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo.
    echo 已在"基带监控"菜单下添加"板卡资源"选项
    echo.
    echo 下一步操作：
    echo 1. 刷新浏览器（Ctrl+F5）
    echo 2. 在左侧菜单找到：基带监控 ^> 板卡资源
    echo 3. 查看板卡资源使用情况
    echo.
    echo ========================================
) else (
    echo.
    echo ========================================
    echo 添加失败，请检查：
    echo 1. 数据库连接信息是否正确
    echo 2. 是否已执行 baseband_menu.sql
    echo 3. 数据库用户是否有足够权限
    echo ========================================
)

echo.
pause
