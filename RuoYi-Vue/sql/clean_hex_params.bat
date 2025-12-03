@echo off
echo ========================================
echo 清理HEX类型参数
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=bbe
set MYSQL_DB=ry-vue

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在清理HEX参数...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < clean_hex_params.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 清理完成！
    echo ========================================
    echo.
    echo 现在可以重新执行 add_hex_params_ordered.bat
    echo.
) else (
    echo.
    echo ========================================
    echo 清理失败，请检查错误信息
    echo ========================================
)

echo.
pause
