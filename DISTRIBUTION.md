# Chika-eInk distribution status

Chika-eInk is an independent Android fork of Chika. Store descriptions under
`fastlane/metadata/android/en-US/` identify this fork; they do not imply a Play or
F-Droid listing. The obsolete inherited F-Droid recipe and upstream release
changelog have been removed. Create version-specific listing metadata only when
preparing an actual fork release.

**Distribution review remains open.** The bundled model and native-library
obligations are documented in [the licensing audit](docs/LICENSING.md).
License texts alone do not complete that review.

Use debug builds for development and device testing. The [release workflow](RELEASING.md)
builds signed candidates from explicitly selected version tags; it does not publish
them automatically. Resolve the documented review before public distribution.

The Android application ID is `io.github.sztkp.chikaeink`, so the fork installs
alongside upstream Chika. It has its own library, settings, progress and backups;
existing upstream/old-ID fork data is not automatically migrated. The immediate
distribution target is a signed APK attached to GitHub Releases.

- [Source](https://github.com/sztkp/chika-eink)
- [Issues](https://github.com/sztkp/chika-eink/issues)
- [Third-party notices](THIRD_PARTY_NOTICES.md)
- [Artwork credits](THIRD_PARTY_NOTICES.md#branding-and-artwork)
