@echo off
echo ========================================
echo 添加帧头标志、起始地址和16进制参数
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=bbe
set MYSQL_DB=ry-vue

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在执行SQL脚本...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < add_frame_header_and_hex_params.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 参数添加成功！
    echo ========================================
    echo.
    echo 已添加内容：
    echo 1. 四个单元类型的帧头标志（0x5555，2字节）
    echo 2. 四个单元类型的起始地址（4字节）
    echo 3. 每个单元类型4个16进制参数
    echo 4. 原有参数的hardware_order已自动调整
    echo.
) else (
    echo.
    echo ========================================
    echo 执行失败，请检查错误信息
    echo ========================================
)

echo.
pause
