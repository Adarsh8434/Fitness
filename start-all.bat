@echo off
setlocal

echo ========================================
echo     FITNESS MICROSERVICES STARTUP
echo ========================================
echo.

call :startService "Config Server" "configserver\configserver" 8080 30
if errorlevel 1 goto :FAILED

call :startService "Eureka Server" "eureka" 8761 30
if errorlevel 1 goto :FAILED

call :startService "AI Service" "aiservice\aiservice" 8083 45
if errorlevel 1 goto :FAILED

call :startService "Activity Service" "activityservice\activityservice" 8082 45
if errorlevel 1 goto :FAILED

call :startService "Gateway" "gateway\gateway" 8085 45
if errorlevel 1 goto :FAILED

call :startService "User Service" "userservice\userservice" 8081 45
if errorlevel 1 goto :FAILED

echo.
echo ========================================
echo   ALL SERVICES STARTED SUCCESSFULLY
echo ========================================
pause
exit /b 0


:startService
set "SERVICE_NAME=%~1"
set "SERVICE_PATH=%~2"
set "SERVICE_PORT=%~3"
set "TIMEOUT=%~4"

echo.
echo ----------------------------------------
echo Starting %SERVICE_NAME% - Port %SERVICE_PORT%
echo ----------------------------------------

start "%SERVICE_NAME%" cmd /k "cd /d %SERVICE_PATH% && mvnw.cmd spring-boot:run"

echo Waiting for %SERVICE_NAME%...

set /a COUNT=0

:CHECK
timeout /t 2 /nobreak > nul

netstat -ano | findstr /R /C:":%SERVICE_PORT% .*LISTENING" > nul

if not errorlevel 1 (
    echo [OK] %SERVICE_NAME% is running on port %SERVICE_PORT%.
    exit /b 0
)

set /a COUNT+=2

if %COUNT% GEQ %TIMEOUT% (
    echo.
    echo [ERROR] %SERVICE_NAME% did not start.
    echo [ERROR] Port %SERVICE_PORT% is not listening.
    exit /b 1
)

echo Waiting... %COUNT%/%TIMEOUT% seconds
goto CHECK


:FAILED
echo.
echo ========================================
echo             STARTUP FAILED
echo ========================================
echo.
echo One of the microservices failed to start.
echo Check the CMD window of the failed service.
echo.
pause
exit /b 1