@echo off
set ANDROID_HOME=E:\sdk
set ANDROID_SDK_ROOT=E:\sdk
start "" /b "E:\sdk\emulator\emulator.exe" -avd PinyinMate_API_36 -no-snapshot -gpu host
