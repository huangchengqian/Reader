# Security Policy

## Supported Versions

LocalReader follows Semantic Versioning. Security fixes are applied to the
latest minor release line.

| Version | Supported          |
| ------- | ------------------ |
| 2.1.x   | :white_check_mark: |
| 2.0.x   | :x:                |
| < 2.0   | :x:                |

If you are on an unsupported release, please upgrade to the latest `2.1.x`
build before reporting a vulnerability.

## Reporting a Vulnerability

Please **do not** open a public GitHub Issue for security-sensitive reports.
Public issues are visible to everyone and can be read by attackers before a fix
ships.

Preferred channel: use GitHub's
[Private Vulnerability Reporting](https://github.com/huangchengqian/Reader/security/advisories/new)
on this repository. The report is delivered privately to the maintainer.

Fallback channel: email `huangchengqian@139.com` with the subject line prefixed
`[LocalReader Security]`. Encrypt sensitive details if your tooling supports
it; PGP keys are not currently published, so prefer the Private Vulnerability
Reporting form when in doubt.

Please include the following in your report:

- Affected version (`versionName` / `versionCode`) and install source.
- Device model, Android version, and any custom ROM details.
- A minimal reproduction: steps, sample file (EPUB / MOBI) if relevant, and
  expected vs. observed behavior.
- Crash logs from `adb logcat` or the in-app crash reporter, redacted of any
  personal data.
- Whether you are willing to be credited in the advisory.

## Response Timeline

- **Acknowledgement**: within 3 business days of receiving the report.
- **Initial assessment**: within 7 business days, including a tentative
  severity rating and reproduction status.
- **Status updates**: every 14 days until the issue is resolved or declined.
- **Disclosure**: coordinated disclosure after a fix is released or after 90
  days, whichever comes first.

## Scope

The following classes of issues are in scope:

- Code execution or sandbox escape when opening an EPUB / MOBI / KF8 file.
- Path traversal or arbitrary file write through crafted book metadata.
- Crash or denial of service affecting the reader, bookshelf, or settings.
- Exposure of reading history, bookmarks, or profile data to other apps.

Out of scope: vulnerabilities in third-party libraries (please report upstream),
issues requiring a rooted or otherwise compromised device, and self-XSS in
debug-only screens.

## Recognition

We are happy to credit reporters in the release notes and the security advisory
unless you ask to remain anonymous. Thank you for keeping LocalReader safe.
