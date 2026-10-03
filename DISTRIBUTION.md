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

The package ID remains `com.chakra.comicreader`. A fork signed with a different key
cannot update an upstream installation. Plan any identity/signing change separately.

- [Source](https://github.com/sztkp/chika-eink)
- [Issues](https://github.com/sztkp/chika-eink/issues)
- [Third-party notices](THIRD_PARTY_NOTICES.md)
- [Artwork credits](THIRD_PARTY_NOTICES.md#branding-and-artwork)
