@echo off
set ANDROID_HOME=E:\sdk
set ANDROID_SDK_ROOT=E:\sdk
"E:\sdk\platform-tools\adb.exe" install -r "E:\PinyinMate\app\build\outputs\apk\debug\app-debug.apk"
"E:\sdk\platform-tools\adb.exe" shell am start -n com.pinyinmate.app/.MainActivity
