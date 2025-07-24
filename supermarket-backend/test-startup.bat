@echo off
echo ========================================
echo 超市管理系统后端启动测试
echo ========================================

echo 1. 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo Java环境未配置，请检查JAVA_HOME
    pause
    exit /b 1
)

echo.
echo 2. 清理并编译项目...
call mvn clean compile -DskipTests
if %errorlevel% neq 0 (
    echo 编译失败，请检查代码
    pause
    exit /b 1
)

echo.
echo 3. 运行测试...
call mvn test -Dtest=ApplicationStartupTest
if %errorlevel% neq 0 (
    echo 启动测试失败
    pause
    exit /b 1
)

echo.
echo ========================================
echo 测试完成！项目可以正常启动
echo ========================================
pause
