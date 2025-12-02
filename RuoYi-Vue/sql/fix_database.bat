@echo off
chcp 65001 >nul
echo ========================================
echo 参数约束表修复工具
echo ========================================
echo.

REM 设置数据库连接信息
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=bbe
set DB_USER=root

echo 请输入数据库密码:
set /p DB_PASS=

echo.
echo 正在连接数据库 %DB_NAME%...
echo.

REM 执行SQL文件
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < fix_param_constraint_table.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 修复成功！
    echo ========================================
    echo.
    echo 表 sys_baseband_param_constraint 已重新创建
    echo 测试数据已插入
    echo.
    echo 请重启Spring Boot应用后测试
    echo.
) else (
    echo.
    echo ========================================
    echo 修复失败！
    echo ========================================
    echo.
    echo 请检查：
    echo 1. MySQL是否已安装并在PATH中
    echo 2. 数据库连接信息是否正确
    echo 3. 数据库用户是否有足够权限
    echo.
)

pause
