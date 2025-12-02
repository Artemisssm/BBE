@echo off
echo ========================================
echo 为参数约束表添加模式类型字段
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=bbe
set MYSQL_DB=ry-vue

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在执行SQL脚本...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < add_mode_types_to_constraint.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 模式类型字段添加成功！
    echo ========================================
) else (
    echo.
    echo ========================================
    echo 执行失败，请检查错误信息
    echo ========================================
)

echo.
pause
