@echo off
echo ===========================================
echo  Building and Packaging Restaurant Management App
echo ===========================================

call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Build failed. Please check the Maven output for details.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ===========================================
echo  Build Successful!
echo  JAR file created at: target\restaurant-management-app.jar
echo ===========================================

echo.
echo To run the application with production profile, use:
echo   java -jar target\restaurant-management-app.jar --spring.profiles.active=prod

echo.
echo To run with custom database settings, use:
echo   java -jar target\restaurant-management-app.jar ^
echo       --spring.profiles.active=prod ^
echo       --spring.datasource.url=JDBC_CONNECTION_STRING ^
echo       --spring.datasource.username=USERNAME ^
echo       --spring.datasource.password=PASSWORD

echo.
pause
