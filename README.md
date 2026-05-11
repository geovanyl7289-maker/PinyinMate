<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher.png" width="112" alt="PinyinMate Logo" />
</p>

<h1 align="center">PinyinMate</h1>

<p align="center">
  一款干净、轻量、类 iOS / macOS 风格的 Android 中文转拼音工具。
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-Native-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.10-10B981?style=for-the-badge" />
</p>

---

## 简介

PinyinMate 是一款原生 Android 中文转拼音工具，使用 Kotlin 与 Jetpack Compose 构建。它专注于把中文内容快速转换为拼音，并提供复制、转发、收藏、历史记录、格式模板、桌面小组件快捷转换等实用功能。

它的目标不是把简单工具做复杂，而是把常用转换流程做得更顺手：打开、输入、转换、复制，一气呵成。所有转换均在本地完成，不上传用户文本内容。

## 亮点

- **本地离线转换**：中文文本直接在设备端转换，保护隐私
- **多种输出格式**：支持声调、数字声调、无声调、小写、大写、首字母大写
- **复制格式模板**：支持纯拼音、中文加拼音、逐行、表格、首字母
- **历史与收藏**：常用结果可收藏，历史记录支持确认删除与清空确认
- **快捷分享**：一键复制或调用系统分享
- **桌面小组件**：复制中文后点击小组件，可快速进入转换
- **深浅色模式**：支持浅色、深色、跟随系统
- **启动动画**：加入品牌启动动画，打开应用更有完整感

## 界面风格

PinyinMate 采用接近 iOS / macOS 的极简视觉语言：

- 大量留白
- 大圆角玻璃卡片
- 柔和阴影
- 悬浮胶囊底部导航栏
- Android 手势导航区域适配
- 平滑页面切换动画

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX
- ICU Transliterator
- Gradle / Android Gradle Plugin

## 构建

使用 Android Studio 打开项目根目录，然后运行 `app`。

也可以使用命令行构建：

```powershell
gradle assembleDebug
```

生成的 debug APK 位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## English

PinyinMate is a native Android pinyin conversion app built with Kotlin and Jetpack Compose. It provides fast local Chinese-to-pinyin conversion with copy templates, history, favorites, sharing, dark mode, a home-screen widget shortcut, and an Apple-inspired minimal interface.

## License

This project is currently published as source code for learning and personal project demonstration.
