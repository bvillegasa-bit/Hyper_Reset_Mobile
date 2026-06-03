#!/usr/bin/env pwsh
<#
    Hyper Reset - Ngrok Setup Helper Script (PowerShell)
    
    This script helps configure and test Ngrok tunnel for real device testing.
    
    Usage: pwsh -ExecutionPolicy Bypass -File setup-ngrok.ps1
#>

$ErrorActionPreference = "Continue"

function Show-Menu {
    Clear-Host
    Write-Host ""
    Write-Host "====================================================" -ForegroundColor Cyan
    Write-Host "  Hyper Reset - Ngrok Real Device Testing Setup" -ForegroundColor Cyan
    Write-Host "====================================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "What would you like to do?" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  1. Start Ngrok tunnel (expose localhost:8080)" -ForegroundColor Green
    Write-Host "  2. View Ngrok web inspector in browser" -ForegroundColor Green
    Write-Host "  3. Show current configuration" -ForegroundColor Green
    Write-Host "  4. Build Android APK for Ngrok testing" -ForegroundColor Green
    Write-Host "  5. Install APK on connected device" -ForegroundColor Green
    Write-Host "  6. View device logs (debugging)" -ForegroundColor Green
    Write-Host "  7. Exit" -ForegroundColor Red
    Write-Host ""
}

function Start-NgrokTunnel {
    Write-Host ""
    Write-Host "Starting Ngrok tunnel on port 8080..." -ForegroundColor Cyan
    Write-Host ""
    Write-Host "IMPORTANT: When Ngrok starts, look for a line like:" -ForegroundColor Yellow
    Write-Host "          Forwarding    https://xxxx-yy-zzzz.ngrok.io -> http://localhost:8080" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Save that URL and use it to configure your Android app!" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Keep this window open while testing. Session lasts 2 hours (free tier)." -ForegroundColor Magenta
    Write-Host ""
    Read-Host "Press Enter to start Ngrok"
    Write-Host ""
    
    ngrok http 8080
}

function Open-WebInspector {
    Write-Host ""
    Write-Host "Opening Ngrok web inspector..." -ForegroundColor Cyan
    Write-Host "URL: http://127.0.0.1:4040" -ForegroundColor Yellow
    Write-Host ""
    
    if (Test-Path "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe") {
        & "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe" "http://127.0.0.1:4040"
    } elseif (Test-Path "C:\Program Files\Google\Chrome\Application\chrome.exe") {
        & "C:\Program Files\Google\Chrome\Application\chrome.exe" "http://127.0.0.1:4040"
    } else {
        Start-Process "http://127.0.0.1:4040"
    }
    
    Start-Sleep -Seconds 2
}

function Show-Config {
    Write-Host ""
    Write-Host "====================================================" -ForegroundColor Cyan
    Write-Host "  Current Ngrok Configuration" -ForegroundColor Cyan
    Write-Host "====================================================" -ForegroundColor Cyan
    Write-Host ""
    
    Write-Host "Ngrok Version:" -ForegroundColor Yellow
    ngrok version
    Write-Host ""
    
    Write-Host "To use Ngrok tunnel:" -ForegroundColor Green
    Write-Host ""
    Write-Host "1. Update Constants.java:" -ForegroundColor Cyan
    Write-Host "   File: app/src/main/java/com/hyperreset/app/utils/Constants.java" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Set these values:" -ForegroundColor Cyan
    Write-Host "   private static final String NGROK_URL = ""https://YOUR-NGROK-URL.ngrok.io/api/"";" -ForegroundColor Gray
    Write-Host "   private static final boolean USE_NGROK = true;" -ForegroundColor Gray
    Write-Host ""
    Write-Host "3. Rebuild:" -ForegroundColor Cyan
    Write-Host "   ./gradlew assembleDebug" -ForegroundColor Gray
    Write-Host ""
    
    Read-Host "Press Enter to continue"
}

function Build-APK {
    Write-Host ""
    Write-Host "Building Android APK for Ngrok testing..." -ForegroundColor Cyan
    Write-Host ""
    Write-Host "First, ensure you've updated Constants.java with:" -ForegroundColor Yellow
    Write-Host "  - USE_NGROK = true" -ForegroundColor Gray
    Write-Host "  - NGROK_URL = ""https://YOUR-NGROK-URL.ngrok.io/api/""" -ForegroundColor Gray
    Write-Host ""
    
    Read-Host "Press Enter to build"
    Write-Host ""
    
    & "./gradlew.bat" assembleDebug
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "Build successful!" -ForegroundColor Green
        Write-Host ""
        Write-Host "APK location:" -ForegroundColor Yellow
        Write-Host "  app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "To install on device:" -ForegroundColor Yellow
        Write-Host "  adb install -r app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Cyan
    } else {
        Write-Host ""
        Write-Host "Build failed. Check errors above." -ForegroundColor Red
    }
    
    Read-Host "Press Enter to continue"
}

function Install-APK {
    Write-Host ""
    Write-Host "Installing APK on connected device..." -ForegroundColor Cyan
    Write-Host ""
    
    Write-Host "Checking for connected devices..." -ForegroundColor Yellow
    adb devices
    
    Write-Host ""
    $proceed = Read-Host "Continue with installation? [Y/n]"
    
    if ($proceed -ne 'n' -and $proceed -ne 'N') {
        Write-Host ""
        Write-Host "Installing..." -ForegroundColor Cyan
        adb install -r "app\build\outputs\apk\debug\app-debug.apk"
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "Installation successful!" -ForegroundColor Green
            Write-Host "App is ready to test on your device." -ForegroundColor Green
        } else {
            Write-Host ""
            Write-Host "Installation failed. Check errors above." -ForegroundColor Red
        }
    }
    
    Read-Host "Press Enter to continue"
}

function View-DeviceLogs {
    Write-Host ""
    Write-Host "Viewing device logs for Hyper Reset app..." -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Filtering for 'hyperreset', 'retrofit', 'okhttp', 'auth'..." -ForegroundColor Yellow
    Write-Host "Press Ctrl+C to stop." -ForegroundColor Magenta
    Write-Host ""
    
    adb logcat | Select-String -Pattern "hyperreset|retrofit|okhttp|auth" -CaseSensitive:$false
}

# Main loop
while ($true) {
    Show-Menu
    $choice = Read-Host "Enter your choice [1-7]"
    
    switch ($choice) {
        "1" { Start-NgrokTunnel }
        "2" { Open-WebInspector }
        "3" { Show-Config }
        "4" { Build-APK }
        "5" { Install-APK }
        "6" { View-DeviceLogs }
        "7" {
            Write-Host ""
            Write-Host "Goodbye!" -ForegroundColor Cyan
            Write-Host ""
            exit
        }
        default {
            Write-Host ""
            Write-Host "Invalid choice. Please try again." -ForegroundColor Red
            Read-Host "Press Enter to continue"
        }
    }
}
