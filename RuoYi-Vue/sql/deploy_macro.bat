@echo off
chcp 65001 >nul
echo ========================================
echo 宏管理模块部署工具
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
echo 正在部署宏管理模块...
echo.

echo [1/2] 创建宏管理表...
mysql -h%db_host% -P%db_port% -u%db_user% -p%db_pass% %db_name% < baseband_macro.sql

if %errorlevel% neq 0 (
    echo ❌ 创建表失败
    goto :error
)
echo ✓ 创建表成功

echo.
echo [2/2] 添加宏管理菜单...
mysql -h%db_host% -P%db_port% -u%db_user% -p%db_pass% %db_name% < add_macro_menu.sql

if %errorlevel% neq 0 (
    echo ❌ 添加菜单失败
    goto :error
)
echo ✓ 添加菜单成功

echo.
echo ========================================
echo ✅ 宏管理模块部署成功！
echo ========================================
echo.
echo 下一步操作：
echo 1. 重启后端服务
echo 2. 刷新浏览器（Ctrl+F5）
echo 3. 在左侧菜单找到：基带监控 ^> 宏管理
echo 4. 开始创建和管理宏配置
echo.
echo ========================================
goto :end

:error
echo.
echo ========================================
echo ❌ 部署失败，请检查：
echo 1. 数据库连接信息是否正确
echo 2. 是否已执行 baseband_menu.sql
echo 3. 数据库用户是否有足够权限
echo ========================================

:end
echo.
pause
