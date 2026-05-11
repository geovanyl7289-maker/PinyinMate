@echo off
setlocal

set "ROOT=%~dp0"
set "APK=%ROOT%app\build\outputs\apk\debug\app-debug.apk"

where adb >nul 2>nul
if %errorlevel%==0 (
    set "ADB=adb"
) else if defined ANDROID_HOME (
    set "ADB=%ANDROID_HOME%\platform-tools\adb.exe"
) else if defined ANDROID_SDK_ROOT (
    set "ADB=%ANDROID_SDK_ROOT%\platform-tools\adb.exe"
) else (
    echo adb was not found. Add Android SDK platform-tools to PATH or set ANDROID_HOME.
    exit /b 1
)

"%ADB%" install -r "%APK%"
"%ADB%" shell am start -n com.pinyinmate.app/.MainActivity
