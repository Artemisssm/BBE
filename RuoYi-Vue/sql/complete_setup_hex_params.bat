@echo off
echo ========================================
echo 完整设置16进制参数
echo 1. 添加排序字段
echo 2. 插入HEX参数（带排序）
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_DB=bbe

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在执行...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < complete_setup_hex_params.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 成功！
    echo ========================================
    echo.
    echo 已完成：
    echo 1. 添加sort_order排序字段
    echo 2. 添加24个16进制参数
    echo 3. 帧头标志和起始地址排序为1和2
    echo.
    echo 下一步：
    echo 1. 重新编译后端（mvn clean package）
    echo 2. 重启后端服务
    echo 3. 刷新浏览器
    echo.
) else (
    echo.
    echo ========================================
    echo 失败！请查看错误信息
    echo ========================================
)

echo.
pause
