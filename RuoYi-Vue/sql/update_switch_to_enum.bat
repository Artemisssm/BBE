@echo off
chcp 65001 >nul
echo ========================================
echo 更新参数类型：SWITCH → ENUM
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
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < update_switch_to_enum_simple.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 更新成功！
    echo ========================================
    echo.
    echo SWITCH类型已改为ENUM类型
    echo 枚举选项：{"0": "关", "1": "开"}
    echo.
    echo 前端界面将使用下拉选择器代替开关控件
    echo.
    echo 下一步：
    echo 1. 刷新浏览器（Ctrl+F5）
    echo 2. 查看参数定义界面，确认类型已更新
    echo 3. 在参数配置界面测试枚举选择器
    echo.
) else (
    echo.
    echo ========================================
    echo 更新失败！
    echo ========================================
    echo.
    echo 请检查：
    echo 1. 数据库连接信息是否正确
    echo 2. 数据库用户是否有足够权限
    echo 3. 是否存在SWITCH类型的参数
    echo.
)

pause
