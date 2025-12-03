@echo off
echo ========================================
echo 有序添加16进制参数
echo 帧头标志和起始地址在最前面
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=bbe
set MYSQL_DB=ry-vue

set /p MYSQL_PWD=请输入MySQL密码: 

echo.
echo 正在执行SQL脚本...
echo 注意：会先删除已存在的HEX类型参数，然后重新插入
echo.
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PWD% %MYSQL_DB% < add_hex_params_ordered.sql

if %errorlevel% == 0 (
    echo.
    echo ========================================
    echo 参数添加成功！
    echo ========================================
    echo.
    echo 已添加内容：
    echo - 4个单元类型
    echo - 每个单元6个16进制参数
    echo   * 帧头标志（0x5555）
    echo   * 起始地址（0x00000000）
    echo   * 4个其他16进制参数
    echo - 总共24个参数
    echo.
    echo 参数顺序：
    echo - 帧头标志和起始地址在最前面
    echo - 其他参数按插入顺序排列
    echo.
) else (
    echo.
    echo ========================================
    echo 执行失败，请检查错误信息
    echo ========================================
)

echo.
pause
