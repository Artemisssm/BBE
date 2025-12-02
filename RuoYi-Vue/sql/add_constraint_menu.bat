@echo off
chcp 65001 >nul
echo ========================================
echo 添加参数约束管理菜单
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
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < add_constraint_menu.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 菜单添加成功！
    echo ========================================
    echo.
    echo 已在"基带监控"菜单下添加"参数约束"选项
    echo.
    echo 下一步操作：
    echo 1. 刷新浏览器（Ctrl+F5）
    echo 2. 在左侧菜单找到：基带监控 ^> 参数约束
    echo 3. 开始管理约束规则
    echo.
) else (
    echo.
    echo ========================================
    echo 菜单添加失败！
    echo ========================================
    echo.
    echo 请检查：
    echo 1. 数据库连接信息是否正确
    echo 2. 是否已执行 baseband_menu.sql
    echo 3. 数据库用户是否有足够权限
    echo.
)

pause
