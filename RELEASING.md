# Releasing Kuro

## Signing

Reuse the existing release key; a replacement key cannot sign updates for installed
releases. Keep the keystore and credentials out of Git and back them up securely.
The ignored `.signing/` directory holds the local backup.

Release certificate SHA-256:
`42e13fe3f6e0c62cc1073a9afaf34ed1ae20a7b594d535d7c3e672c3f954e792`.

For initial setup only:

```bash
keytool -genkeypair -v -keystore kuro-release.jks -alias kuro \
  -keyalg RSA -keysize 4096 -validity 10000
base64 < kuro-release.jks > keystore.b64
```

| Credential | GitHub Actions secret | Local build variable |
| --- | --- | --- |
| Keystore | `KEYSTORE_BASE64` (base64 contents) | `KEYSTORE_FILE` (absolute path) |
| Store password | `KEYSTORE_PASSWORD` | `KEYSTORE_PASSWORD` |
| Signing alias | `KEY_ALIAS` | `KEY_ALIAS` |
| Key password | `KEY_PASSWORD` | `KEY_PASSWORD` |

## Release checklist

1. Review unresolved items in [docs/LICENSING.md](docs/LICENSING.md).
2. Set the fallback version in `app/build.gradle.kts` and commit the intended source.
   Use a tag `vMAJOR.MINOR.PATCH`, optionally with a prerelease suffix such as
   `v0.3.1-eink.1`. `VERSION_NAME` overrides the fallback; the workflow uses the tag
   without `v`. Version code is `MAJOR*10000 + MINOR*100 + PATCH`, ignoring suffixes.
   There is no automatic version bump; prereleases of the same base version share a code.
3. Create and push the tag on that commit. Run the manual
   [Release candidate workflow](.github/workflows/release.yml) with the tag.
   It checks out tagged source, runs both test suites and lint, and builds signed
   APK/AAB files. Tags do not trigger it automatically.
4. Download the `kuro-<version>-candidate` Actions artifact. Verify the version,
   signing certificate, packaged notices, and native-library alignment for the
   intended channel. Test the minified APK on a device, including import and reading.
5. Prepare matching application/dependency source, notices, release notes, and
   SHA-256 checksums. Preserve the R8 mapping with its binary for crash diagnosis.
6. Publish the signed APK and supporting files through GitHub Releases. The workflow
   uploads artifacts only; it does not publish releases. Update store metadata in
   `fastlane/metadata/android/en-US/` only for an actual listing; those files do not
   establish Play or F-Droid availability.

## Local release build

Set the signing variables above through your shell or secret manager, then run:

```bash
VERSION_NAME=0.3.0 ./gradlew :app:assembleRelease :app:bundleRelease
```

| Output | Path |
| --- | --- |
| Signed APK | `app/build/outputs/apk/release/app-release.apk` |
| AAB | `app/build/outputs/bundle/release/app-release.aab` |
| R8 mapping | `app/build/outputs/mapping/release/mapping.txt` |

Without signing variables, release outputs are unsigned; the APK is
`app-release-unsigned.apk`. Debug builds use the standard debug key.
