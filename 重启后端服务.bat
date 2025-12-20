@echo off
echo ========================================
echo 重启后端服务
echo ========================================
echo.

cd RuoYi-Vue\ruoyi-admin

echo 正在编译项目...
call mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo.
    echo 编译失败！请检查错误信息。
    pause
    exit /b 1
)

echo.
echo 编译成功！
echo.
echo 请手动启动后端服务：
echo   方法1: 在IDE中运行 RuoYiApplication.java
echo   方法2: 执行命令 mvn spring-boot:run
echo.
pause
