@echo off
setlocal

if "%~1"=="" (
    set "AVD_NAME=PinyinMate_API_36"
) else (
    set "AVD_NAME=%~1"
)

where emulator >nul 2>nul
if %errorlevel%==0 (
    set "EMULATOR=emulator"
) else if defined ANDROID_HOME (
    set "EMULATOR=%ANDROID_HOME%\emulator\emulator.exe"
) else if defined ANDROID_SDK_ROOT (
    set "EMULATOR=%ANDROID_SDK_ROOT%\emulator\emulator.exe"
) else (
    echo emulator was not found. Add Android SDK emulator to PATH or set ANDROID_HOME.
    exit /b 1
)

start "" /b "%EMULATOR%" -avd "%AVD_NAME%" -no-snapshot -gpu host
