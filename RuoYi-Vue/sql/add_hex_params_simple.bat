@echo off
echo ========================================
echo 添加16进制参数（简化版）
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=bbe
set MYSQL_DB=ry-vue

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在执行SQL脚本...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < add_hex_params_simple.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 参数添加成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo 执行失败，请检查错误信息
    echo ========================================
)

echo.
pause
