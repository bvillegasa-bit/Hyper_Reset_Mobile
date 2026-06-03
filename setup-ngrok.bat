@echo off
REM Hyper Reset - Ngrok Setup Helper Script
REM This script helps configure and test Ngrok tunnel for real device testing

setlocal enabledelayedexpansion

echo.
echo ====================================================
echo   Hyper Reset - Ngrok Real Device Testing Setup
echo ====================================================
echo.

:MENU
echo.
echo What would you like to do?
echo.
echo  1. Start Ngrok tunnel (expose localhost:8080)
echo  2. View Ngrok web inspector
echo  3. Show current configuration
echo  4. Build Android APK for Ngrok testing
echo  5. Exit
echo.
set /p choice="Enter your choice [1-5]: "

if "%choice%"=="1" goto START_NGROK
if "%choice%"=="2" goto WEB_INSPECTOR
if "%choice%"=="3" goto SHOW_CONFIG
if "%choice%"=="4" goto BUILD_APK
if "%choice%"=="5" goto EXIT_SCRIPT

echo Invalid choice. Please try again.
goto MENU

:START_NGROK
echo.
echo Starting Ngrok tunnel on port 8080...
echo.
echo NOTE: When Ngrok starts, look for a line like:
echo       Forwarding     https://xxxx-yy-zzzz.ngrok.io ^-^> http://localhost:8080
echo.
echo Save that URL and use it to configure your Android app!
echo.
pause
ngrok http 8080
goto MENU

:WEB_INSPECTOR
echo.
echo Opening Ngrok web inspector in browser...
echo.
start http://127.0.0.1:4040
timeout /t 2 /nobreak
goto MENU

:SHOW_CONFIG
echo.
echo ====================================================
echo   Current Ngrok Configuration
echo ====================================================
echo.
echo Status:
ngrok version
echo.
echo To use Ngrok tunnel:
echo.
echo 1. Update Constants.java with your Ngrok URL:
echo    File: app/src/main/java/com/hyperreset/app/utils/Constants.java
echo.
echo 2. Set these values:
echo    private static final String NGROK_URL = "https://YOUR-NGROK-URL.ngrok.io/api/";
echo    private static final boolean USE_NGROK = true;
echo.
echo 3. Then rebuild:
echo    ./gradlew assembleDebug
echo.
pause
goto MENU

:BUILD_APK
echo.
echo Building Android APK for Ngrok testing...
echo.
echo First, ensure you've updated Constants.java with:
echo   - USE_NGROK = true
echo   - NGROK_URL = "https://YOUR-NGROK-URL.ngrok.io/api/"
echo.
pause
call gradlew assembleDebug
if %errorlevel% equ 0 (
    echo.
    echo Build successful!
    echo.
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo To install on device:
    echo   adb install -r app\build\outputs\apk\debug\app-debug.apk
) else (
    echo.
    echo Build failed. Check errors above.
)
pause
goto MENU

:EXIT_SCRIPT
echo.
echo Goodbye!
exit /b 0
