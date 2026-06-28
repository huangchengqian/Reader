# LocalReader

A fully offline ebook reader for Android. Built with Kotlin and Jetpack Compose.

<p align="left">
  <img src="./social-preview.svg" alt="LocalReader" width="640" />
</p>

[![Build](https://img.shields.io/github/actions/workflow/status/huangchengqian/Reader/android-ci.yml?branch=main&style=flat-square)](https://github.com/huangchengqian/Reader/actions)
[![License](https://img.shields.io/github/license/huangchengqian/Reader?style=flat-square)](./LICENSE)
[![Release](https://img.shields.io/github/v/release/huangchengqian/Reader?style=flat-square)](https://github.com/huangchengqian/Reader/releases)
[![Stars](https://img.shields.io/github/stars/huangchengqian/Reader?style=flat-square)](https://github.com/huangchengqian/Reader/stargazers)
[![Android](https://img.shields.io/badge/Android-8.0%2B-brightgreen?style=flat-square&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blueviolet?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Compose-BOM%202024.08-4285F4?style=flat-square&logo=android)](https://developer.android.com/jetpack/compose)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](./CONTRIBUTING.md)

LocalReader 把你的电子书书架放进设备本地:不联网、不上传、零账号。所有阅读数据、阅读进度、书签都存储在你自己的设备上。

LocalReader keeps your ebook library on-device. No network calls, no account, no upload. Every bookmark, every minute of reading time, every reading position lives on your own device.

---

## 中文说明

### 项目简介

LocalReader 是一款面向 Android 8.0 及以上设备的开源离线电子书阅读器。当前版本 `2.1.0` (`versionCode 13`)。应用采用 Kotlin 2.0 与 Jetpack Compose 构建,基于 Material 3 设计语言,目标是为读者提供一个纯粹、安静、不打扰的本地阅读环境。

应用完全不申请 `INTERNET` 权限,这意味着 LocalReader 永远不会把你的阅读行为、书库内容或个人数据传送到任何远程服务器。你可以放心地把私有书籍、个人笔记、未公开草稿放进书架。

### 核心特性

- 完全离线架构:应用清单中不包含 `INTERNET` 权限,所有文件读取、解析、存储都在本地完成。
- EPUB 解析:支持标准 EPUB 2 / EPUB 3 电子书,涵盖重构排版后的章节内容、元数据、目录。
- 自研 MOBI / KF8 / KF6 解析器:位于 `app/src/main/java/com/localreader/lib/mobi/`,完整覆盖 Kindle 早期 MOBI 格式与较新的 KF8 (相当于 EPUB 3 inside MOBI) 容器,可读取章节、文本、图片、封面元数据。
- Material 3 主题:浅色、深色、跟随系统三种模式自由切换,色彩与排版遵循 Material You 规范。
- 阅读统计:自动记录每次阅读会话时长、阅读进度、读完书籍数量,在统计页汇总展示。
- 阅读进度同步:每本书的当前位置、章节、阅读百分比保存在 Room 数据库中,下次打开自动回到上次位置。
- 书签:在阅读过程中随时添加书签,书签可命名、可跳转、可删除。
- 自定义字体与背景:支持导入外部字体 (`.ttf` / `.otf`),自定义背景颜色与背景图片。
- 单 Activity + Compose Navigation:四大主屏幕 (书架、阅读器、统计、个人) 通过 Compose Navigation 串联,启动延迟低。
- Room 数据库:五个核心实体 (`Book`、`Bookmark`、`ReadingProgress`、`ReadingSession`、`UserProfile`) 完整支撑阅读历史与个人偏好。
- DataStore 偏好存储:主题、字号、字体等 UI 偏好使用 Preferences DataStore 持久化。

### 截图

> 真实截图将于首个 GitHub Release 发布后补充。当前版本请通过 `app-debug.apk` 在本地体验完整界面。

```
[ Bookshelf ]               [ Reader ]                 [ Statistics ]           [ Profile ]
+-----------------+   +----------------------+   +------------------+   +------------------+
| Import          |   | Chapter 3            |   | 12 h 35 m        |   | LocalReader      |
|   Moby Dick     |   |                      |   | total reading    |   |   huangchengqian |
|   Pride & Prejudice | Call me Ishmael.   |   |                  |   |                  |
|   The Old Man... |   | Some years ago...    |   | 4 books finished |   | Theme: Dark      |
|   ...           |   | [progress 38%]       |   | Last 7 days      |   | Font: Default    |
+-----------------+   +----------------------+   +------------------+   +------------------+
```

### 技术栈

| 类别 | 选型 |
| --- | --- |
| 语言 | Kotlin 2.0.0 |
| 构建 | Android Gradle Plugin 8.5.2,Gradle Wrapper 8.9 |
| JDK | Temurin 21 |
| UI | Jetpack Compose (BOM 2024.08.00) + Material 3 |
| 导航 | Navigation Compose 2.7.7 |
| 数据库 | Room 2.6.1 (通过 KSP 注解处理) |
| 图片加载 | Coil 2.7.0 |
| 偏好存储 | DataStore Preferences 1.1.1 |
| 协程 | kotlinx-coroutines 1.8.1 |
| 系统 UI | Accompanist SystemUiController 0.34.0 |
| 最低 SDK | 26 (Android 8.0 Oreo) |
| 目标 SDK | 35 (Android 15) |
| 编译 SDK | 35 |

### 构建与安装

环境准备:JDK 21、Android Studio Koala (2024.1.1) 或更新版本、Android SDK Platform 35、本地 `local.properties` (含 `sdk.dir` 指向 SDK 路径,该文件已在 `.gitignore` 中)。

克隆仓库:

```bash
git clone https://github.com/huangchengqian/Reader.git
cd Reader
```

Debug 构建 (生成可调试 APK):

```bash
./gradlew assembleDebug
```

产物位于 `app/build/outputs/apk/debug/app-debug.apk`,可直接 `adb install` 到设备。

Release 构建 (未签名):

```bash
./gradlew assembleRelease
```

Release 构建产物需要自行签名,详见路线图。

运行单元测试与 Lint 检查:

```bash
./gradlew testDebugUnitTest lint
```

### 路线图

- i18n:补全 `values-en/` 等多语言资源,完善英文界面文案与翻译校对流程。
- 真实截图:首个 GitHub Release 后补充书架、阅读器、统计、个人四张主屏幕截图。
- 签名流水线:接入 GitHub Actions 自动构建签名 Release APK 并发布到 GitHub Releases。
- 单元测试骨架:在 `app/src/test/` 下建立 `PackageSanityTest` 等基础测试样例。
- 文档站:为 `lib/mobi/` 自研解析器撰写单独的架构说明,便于后续贡献者上手。

### 参与贡献

欢迎以 Issue 报告 Bug,以 Pull Request 提交修复或新功能。请先阅读 [`CONTRIBUTING.md`](./CONTRIBUTING.md) 了解本地构建、代码风格与 PR 流程。提交前请运行 `./gradlew testDebugUnitTest lint` 确认本地检查通过。

### 开源协议

本项目基于 **MIT License** 开源,详见 [`LICENSE`](./LICENSE) 文件。

### Star History

如果你喜欢这个项目,欢迎点一颗 Star。

[![Star History Chart](https://api.star-history.com/svg?repos=huangchengqian/Reader&type=Date)](https://star-history.com/#huangchengqian/Reader&Date)

---

## English

### Overview

LocalReader is an open-source, fully offline ebook reader for Android 8.0 and above. The current release is `2.1.0` (`versionCode 13`). The app is written in Kotlin 2.0 with Jetpack Compose and Material 3, designed to give readers a calm, distraction-free, local reading experience.

The application manifest deliberately omits the `INTERNET` permission. LocalReader will never transmit your reading activity, library contents, or personal data to any remote server. Private manuscripts, draft notes, and personal documents can sit on your bookshelf without worry.

### Key Features

- Fully offline architecture: the app manifest does not declare `INTERNET`. All file I/O, parsing, and persistence happen locally.
- EPUB parsing: supports EPUB 2 and EPUB 3, including reflowable chapter content, metadata, and table of contents.
- Custom MOBI / KF8 / KF6 parser: implemented from scratch under `app/src/main/java/com/localreader/lib/mobi/`. Covers legacy MOBI plus the modern KF8 (EPUB 3 packaged inside MOBI) container, extracting chapter text, images, and cover metadata.
- Material 3 theming: light, dark, and follow-system modes with colors and typography following Material You.
- Reading statistics: each reading session is recorded automatically. Total time, progress, and finished books roll up on the Statistics screen.
- Reading progress sync: current position, last chapter, and percentage per book are stored in Room and restored on next open.
- Bookmarks: add, name, jump to, and delete bookmarks at any point in the text.
- Custom fonts and backgrounds: import external `.ttf` / `.otf` files and choose custom background colors or images.
- Single-Activity + Compose Navigation: four main screens (Bookshelf, Reader, Statistics, Profile) wired through Compose Navigation with fast cold-start.
- Room database: five core entities (`Book`, `Bookmark`, `ReadingProgress`, `ReadingSession`, `UserProfile`) back the full reading history.
- DataStore preferences: theme, font size, font family, and other UI preferences persist through Preferences DataStore.

### Screenshots

> Real screenshots will be added once the first GitHub Release ships. Until then, build and install `app-debug.apk` locally to see every screen in action.

```
[ Bookshelf ]               [ Reader ]                 [ Statistics ]           [ Profile ]
+-----------------+   +----------------------+   +------------------+   +------------------+
| Import          |   | Chapter 3            |   | 12 h 35 m        |   | LocalReader      |
|   Moby Dick     |   |                      |   | total reading    |   |   huangchengqian |
|   Pride & Prejudice | Call me Ishmael.   |   |                  |   |                  |
|   The Old Man... |   | Some years ago...    |   | 4 books finished |   | Theme: Dark      |
|   ...           |   | [progress 38%]       |   | Last 7 days      |   | Font: Default    |
+-----------------+   +----------------------+   +------------------+   +------------------+
```

### Tech Stack

| Category | Selection |
| --- | --- |
| Language | Kotlin 2.0.0 |
| Build | Android Gradle Plugin 8.5.2, Gradle Wrapper 8.9 |
| JDK | Temurin 21 |
| UI | Jetpack Compose (BOM 2024.08.00) + Material 3 |
| Navigation | Navigation Compose 2.7.7 |
| Database | Room 2.6.1 (annotation-processed via KSP) |
| Image loading | Coil 2.7.0 |
| Preferences | DataStore Preferences 1.1.1 |
| Coroutines | kotlinx-coroutines 1.8.1 |
| System UI | Accompanist SystemUiController 0.34.0 |
| Min SDK | 26 (Android 8.0 Oreo) |
| Target SDK | 35 (Android 15) |
| Compile SDK | 35 |

### Build and Install

Prerequisites: JDK 21, Android Studio Koala (2024.1.1) or newer, Android SDK Platform 35, and a local `local.properties` pointing `sdk.dir` at your SDK installation (this file is gitignored).

Clone the repository:

```bash
git clone https://github.com/huangchengqian/Reader.git
cd Reader
```

Build a debug APK:

```bash
./gradlew assembleDebug
```

The output sits at `app/build/outputs/apk/debug/app-debug.apk` and can be installed with `adb install`.

Build an unsigned release APK:

```bash
./gradlew assembleRelease
```

Release artifacts must be signed manually; see the roadmap below.

Run unit tests and lint:

```bash
./gradlew testDebugUnitTest lint
```

### Roadmap

- i18n: complete `values-en/` and additional locales, polish English strings, and add a translation review pass.
- Real screenshots: capture and publish the four main screens once the first GitHub Release ships.
- Signing pipeline: wire up GitHub Actions to build signed Release APKs and publish them to GitHub Releases.
- Unit test skeleton: stand up `PackageSanityTest` and friends under `app/src/test/`.
- Docs site: document the architecture of the custom `lib/mobi/` parser so new contributors can onboard quickly.

### Contributing

Bug reports via Issues and fixes via Pull Requests are welcome. Read [`CONTRIBUTING.md`](./CONTRIBUTING.md) for local build instructions, code style, and the PR workflow. Run `./gradlew testDebugUnitTest lint` before opening a PR.

### License

This project is released under the **MIT License**. See [`LICENSE`](./LICENSE) for the full text.

### Star History

If LocalReader makes your reading life calmer, a Star goes a long way.

[![Star History Chart](https://api.star-history.com/svg?repos=huangchengqian/Reader&type=Date)](https://star-history.com/#huangchengqian/Reader&Date)
