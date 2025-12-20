@echo off
chcp 65001 >nul
echo ========================================
echo 宏管理菜单修复工具
echo ========================================
echo.
echo 此工具将：
echo 1. 删除重复的宏管理菜单
echo 2. 重新创建正确的菜单结构
echo 3. 验证修复结果
echo.
pause

set /p db_host="请输入数据库地址 (默认: localhost): "
if "%db_host%"=="" set db_host=localhost

set /p db_port="请输入数据库端口 (默认: 3306): "
if "%db_port%"=="" set db_port=3306

set /p db_name="请输入数据库名称 (默认: ry-vue): "
if "%db_name%"=="" set db_name=ry-vue

set /p db_user="请输入数据库用户名 (默认: root): "
if "%db_user%"=="" set db_user=root

set /p db_pass="请输入数据库密码: "

echo.
echo 正在修复宏管理菜单...
echo.

mysql -h%db_host% -P%db_port% -u%db_user% -p%db_pass% %db_name% < fix_macro_menu.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo ✅ 修复成功！
    echo ========================================
    echo.
    echo 下一步操作：
    echo 1. 清除浏览器缓存（Ctrl+Shift+Delete）
    echo 2. 关闭所有浏览器标签
    echo 3. 重新打开浏览器并登录
    echo 4. 检查"基带监控 ^> 宏管理"菜单
    echo.
    echo 如果仍有问题，请查看：宏管理问题排查和修复指南.md
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ❌ 修复失败
    echo ========================================
    echo.
    echo 请检查：
    echo 1. 数据库连接信息是否正确
    echo 2. 数据库用户是否有足够权限
    echo 3. 数据库服务是否正常运行
    echo.
    echo 详细排查步骤请查看：宏管理问题排查和修复指南.md
    echo ========================================
)

echo.
pause
