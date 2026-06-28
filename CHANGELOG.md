# Changelog

All notable changes to LocalReader will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial i18n scaffolding with English locale under `app/src/main/res/values-en/`.
- GitHub Actions CI pipeline that builds the debug APK and runs unit tests on every push and pull request.
- `PackageSanityTest` skeleton under `app/src/test/java/com/localreader/util/` to validate the JUnit test infrastructure.

### Changed

- None.

### Removed

- None.

## [2.1.0] - 2026-04-12

### Added

- Fully offline architecture: removed all network dependencies; the app manifest no longer declares `INTERNET`.
- EPUB parser for EPUB 2 and EPUB 3 ebooks, including chapter content, metadata, and table of contents.
- Custom MOBI / KF8 / KF6 parser implemented from scratch under `app/src/main/java/com/localreader/lib/mobi/`.
- Material 3 theming with light, dark, and follow-system modes.
- Reading statistics screen with per-session tracking, total reading time, and completed-book counters.
- Reading progress sync that restores the last position, chapter, and percentage per book.
- Bookmark management with add, name, jump, and delete actions.
- Custom font and background support, including `.ttf` / `.otf` font import.
- Single-Activity architecture using Compose Navigation across Bookshelf, Reader, Statistics, and Profile.
- Room database with five entities: `Book`, `Bookmark`, `ReadingProgress`, `ReadingSession`, `UserProfile`.
- DataStore Preferences for theme, font size, font family, and other UI preferences.

[Unreleased]: https://github.com/huangchengqian/Reader/compare/v2.1.0...HEAD
[2.1.0]: https://github.com/huangchengqian/Reader/releases/tag/v2.1.0
