# Contributing to LocalReader

Thanks for your interest in making LocalReader better. Every bug report,
documentation tweak, parser fix, or new feature adds real value, and this guide
will help you get a contribution merged quickly and painlessly.

## Table of Contents

- [Welcome](#welcome)
- [Reporting Issues](#reporting-issues)
- [Pull Requests](#pull-requests)
- [Local Setup](#local-setup)
- [Project Structure](#project-structure)
- [Code Style](#code-style)
- [Testing](#testing)
- [Adding a New Locale](#adding-a-new-locale)
- [Code of Conduct](#code-of-conduct)

## Welcome

LocalReader is a single-maintainer open-source project at the moment. The
maintainer triages Issues, reviews Pull Requests, and ships releases on a
best-effort basis. Be patient, be kind, and please search before you file a
duplicate. If you want to take on something non-trivial, open an Issue first
to align on scope before you write code.

## Reporting Issues

Before opening a new Issue:

1. Search [existing Issues](https://github.com/huangchengqian/Reader/issues)
   for the same symptom.
2. Check [closed Issues](https://github.com/huangchengqian/Reader/issues?q=is%3Aissue+is%3Aclosed)
   for an answer that already landed.
3. Reproduce on the latest `main` build when possible.

Use the provided Issue templates:

- **Bug reports**: pick the `[BUG]` template, include reproduction steps,
  device info, and `adb logcat` output.
- **Feature requests**: pick the `[FEATURE]` template and describe the
  problem before the proposed solution.

For security-sensitive findings, follow [`SECURITY.md`](./SECURITY.md) instead
of opening a public Issue.

## Pull Requests

1. Fork the repository and create a branch off `main`.
2. Make your change. Keep commits small and write descriptive messages.
3. Run the local checks (see below) and fix anything they flag.
4. Push your branch and open a Pull Request using the provided template.
5. Wait for CI to finish. The PR template's Checklist mirrors the same
   items CI runs.
6. Address review feedback. Squash fix-up commits before merge when
   requested.

Draft PRs are welcome for early feedback. Mark them as `Work in Progress` in
the title so reviewers know not to do a deep review yet.

## Local Setup

Prerequisites:

- **JDK 21** (Temurin or any other distribution that ships 21).
- **Android Studio Koala (2024.1.1)** or newer. Ladybug and Meerkat work too.
- **Android SDK Platform 35** plus the standard build tools installed
  through the SDK Manager.
- **Kotlin 2.0** toolchain is bundled with Android Gradle Plugin 8.5.2, no
  manual install required.

Configure your local SDK path by creating `local.properties` (gitignored) at
the repository root:

```properties
sdk.dir=/absolute/path/to/Android/Sdk
```

Build commands (all run from the repository root):

```bash
# Debug APK, runs the KSP pipeline for Room.
./gradlew assembleDebug

# Unit tests on the debug variant.
./gradlew testDebugUnitTest

# Android Lint on the debug variant.
./gradlew lint

# All three in one shot.
./gradlew assembleDebug testDebugUnitTest lint
```

The CI workflow at `.github/workflows/android-ci.yml` runs the exact same
`assembleDebug testDebugUnitTest lint` triple on every push and PR.

## Project Structure

```
app/
  src/main/java/com/localreader/
    MainActivity.kt          # Single Activity host
    data/                    # Room entities, DAOs, repositories
    lib/mobi/                # Custom MOBI / KF8 / KF6 parser
      MobiReader.kt          # Top-level entry
      MobiBook.kt
      KF8Book.kt
      KF6Book.kt
      PDBFile.kt
      utils/                 # Pure-Kotlin ByteBuffer / bitwise helpers
    ui/                      # Compose screens, themes, components
      bookshelf/
      reader/
      statistics/
      profile/
      theme/                 # Material 3 ColorScheme, Typography
    util/                    # Cross-cutting helpers
  src/main/res/
    values/strings.xml       # Source-of-truth strings (zh-CN)
    values-en/strings.xml    # English translations
  src/test/                  # JVM unit tests
build.gradle.kts             # Root Gradle config
settings.gradle.kts
gradle.properties            # JVM args, AndroidX flags (cleaned up)
```

Most parser logic in `lib/mobi/` is plain Kotlin (only `java.nio.ByteBuffer`)
and is therefore unit-test friendly on the JVM without an emulator.

## Code Style

- Follow the [Kotlin official coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Use four-space indentation, no tabs.
- Prefer expression bodies and immutable `val` over `var`.
- Compose: keep composables small, hoist state, pass plain lambdas down.
- Imports: use Android Studio's default Optimize Imports; do not reorder
  manually.
- Strings: every user-visible string must live in `res/values/strings.xml`.
  Do not inline literals in composables.

Run `./gradlew lint` before pushing. Lint warnings about icons, content
descriptions, and missing translations should be addressed in the same PR.

## Testing

LocalReader is in the early stages of building a test suite. The current
skeleton lives at `app/src/test/java/com/localreader/util/PackageSanityTest.kt`
and validates that the JUnit infrastructure is wired correctly. Future
contributions should expand coverage into:

- `lib/mobi/utils/` parser helpers (pure Kotlin, JVM-friendly).
- `data/` Room DAOs (use the in-memory database builder under test).
- ViewModel state transitions in `ui/`.

Run the full unit test suite with:

```bash
./gradlew testDebugUnitTest
```

## Adding a New Locale

1. Copy `app/src/main/res/values/strings.xml` to
   `app/src/main/res/values-<locale>/strings.xml` (for example
   `values-ja/` for Japanese).
2. Translate every key. Preserve the `app_name` value as-is unless you intend
   to rename the app for that locale.
3. Keep XML entities and Android string-format placeholders (`%1$s`, `%1$d`)
   intact.
4. Run the app on a device set to the target locale and confirm every screen
   renders without overflow.
5. Open a PR titled `[i18n] Add <locale> translation`.

## Code of Conduct

All contributors are expected to follow
[`CODE_OF_CONDUCT.md`](./CODE_OF_CONDUCT.md). Please read it before your first
contribution. Reports of unacceptable behavior can be filed via GitHub Issues.
