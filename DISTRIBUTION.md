# Distributing Chika-eInk

Chika-eInk is an independent fork of [Chika](https://github.com/batunii/chika) by
Chakra (Chalchitra Krida). Use this fork's identity, source repository, and notices
when distributing its builds. This document does not claim an existing store or
F-Droid listing, or acceptance by any distribution service.

- Source: <https://github.com/sztkp/chika-eink>
- Issues: <https://github.com/sztkp/chika-eink/issues>
- Releases: <https://github.com/sztkp/chika-eink/releases>
- Source license: [MPL-2.0](LICENSE).
- Retained dependency/model/font notices: [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md).
- Fork icon provenance and removed artwork: [docs/ARTWORK.md](docs/ARTWORK.md).
- Signing and release workflow: [RELEASING.md](RELEASING.md).

## Direct APK distribution

Build with `./gradlew :app:assembleDebug` for local testing. The APK is at
`app/build/outputs/apk/debug/app-debug.apk`. For signed releases, configure this
repository's signing secrets before using the inherited tag-triggered release workflow.
Make the corresponding source and notices available alongside distributed builds.

## Listing metadata

`fastlane/metadata/android/en-US/` contains fork-specific title/descriptions and the
original fork icon. Inherited screenshots were removed; add new screenshots only using
comics with documented permission for redistribution or original synthetic artwork.

`fdroid/com.chakra.comicreader.yml` is an inherited template pointing to this fork.
Its example versions/tags require review before submission. Review each service's current
requirements and the inherited model/dependency notices before submitting.

The package ID remains `com.chakra.comicreader`. Separate signing keys affect whether
an APK can replace an installed upstream build. Changing the package ID is outside this
cleanup and should be planned with installation and data compatibility in mind.
