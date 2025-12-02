@echo off
chcp 65001 >nul
echo ========================================
echo 升级参数约束表
echo ========================================
echo.
echo 此操作将：
echo 1. 添加新字段：action_type, action_value, ui_effect
echo 2. 修改source_param_name允许为NULL
echo 3. 添加索引
echo 4. 插入示例约束数据
echo.
echo 警告：请先备份数据库！
echo.
pause
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
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% %DB_NAME% < upgrade_constraint_table.sql

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo 升级成功！
    echo ========================================
    echo.
    echo 已完成：
    echo ✓ 添加新字段
    echo ✓ 修改字段约束
    echo ✓ 添加索引
    echo ✓ 插入示例数据
    echo.
    echo 新增约束类型：
    echo 1. 值范围约束 - 影响参数范围
    echo 2. 固定值约束 - 固定参数值
    echo 3. 禁用约束 - 禁用参数操作
    echo 4. 枚举限制约束 - 限制枚举选项
    echo 5. 值同步约束 - 参数值同步
    echo 6. 链路模式约束 - 仅基于链路模式
    echo.
    echo 下一步：
    echo 1. 重启Spring Boot应用
    echo 2. 刷新浏览器
    echo 3. 在约束管理界面查看新功能
    echo.
) else (
    echo.
    echo ========================================
    echo 升级失败！
    echo ========================================
    echo.
    echo 请检查：
    echo 1. 数据库连接信息是否正确
    echo 2. 数据库用户是否有足够权限
    echo 3. 表是否已存在
    echo 4. 是否已备份数据
    echo.
)

pause
