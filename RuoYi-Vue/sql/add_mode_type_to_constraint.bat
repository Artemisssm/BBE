@echo off
chcp 65001 >nul
echo ========================================
echo 添加模式类型字段到约束表
echo ========================================
echo.
echo 此操作将：
echo 1. 添加 source_mode_type 字段
echo 2. 添加 target_mode_type 字段
echo 3. 添加索引
echo 4. 插入示例数据
echo.

REM 设置数据库连接信息
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=ry-vue
set DB_USER=root

echo 请输入数据库密码:
set /p DB_PASS=

echo.
echo 正在连接数据库 %DB_NAME%...
echo.

REM 执行SQL文件
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < add_mode_type_to_constraint.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 添加成功！
    echo ========================================
    echo.
    echo 已完成：
    echo ✓ 添加 source_mode_type 字段
    echo ✓ 添加 target_mode_type 字段
    echo ✓ 添加索引
    echo ✓ 插入示例数据
    echo.
    echo 模式类型选项：
    echo - KSA
    echo - KMA
    echo - SSA
    echo - 基带数传
    echo - * (通配符，适用所有模式)
    echo.
    echo 下一步：
    echo 1. 重启Spring Boot应用
    echo 2. 刷新浏览器
    echo 3. 在约束管理界面可以看到模式类型字段
    echo.
) else (
    echo.
    echo ========================================
    echo 添加失败！
    echo ========================================
    echo.
    echo 请检查：
    echo 1. 数据库连接信息是否正确
    echo 2. 数据库用户是否有足够权限
    echo 3. 表是否已存在
    echo.
)

pause
