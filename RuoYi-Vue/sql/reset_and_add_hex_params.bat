@echo off
echo ========================================
echo 重置并添加16进制参数
echo 先清理，再添加
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_DB=bbe

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo ========================================
echo 步骤1: 清理已存在的HEX参数
echo ========================================
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% -e "DELETE FROM sys_baseband_param_def WHERE value_type = 'HEX';"

if %errorlevel% == 0 (
    echo 清理成功！
) else (
    echo 清理失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo 步骤2: 添加新的HEX参数
echo ========================================
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < add_hex_params_ordered.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 全部完成！
    echo ========================================
    echo.
    echo 已成功添加：
    echo - 4个单元类型
    echo - 每个单元6个16进制参数
    echo - 帧头标志和起始地址在最前面
    echo - 总共24个参数
    echo.
) else (
    echo.
    echo ========================================
    echo 添加失败，请检查错误信息
    echo ========================================
)

echo.
pause
