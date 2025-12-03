@echo off
echo ========================================
echo 强制清理并添加16进制参数
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_DB=bbe

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在执行...
echo 1. 逐个删除可能存在的参数
echo 2. 按顺序添加新参数
echo.

mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < force_clean_and_add.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 成功！
    echo ========================================
    echo.
    echo 已添加24个16进制参数
    echo 帧头标志和起始地址在最前面
    echo.
    echo 现在可以刷新浏览器查看效果
    echo.
) else (
    echo.
    echo ========================================
    echo 失败！请查看错误信息
    echo ========================================
)

echo.
pause
