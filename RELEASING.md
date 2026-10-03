# Building Chika-eInk release candidates

Distribution review remains open; see [the licensing audit](docs/LICENSING.md).
The manual [Release candidate workflow](.github/workflows/release.yml) builds
signed APK/AAB candidates and uploads them as Actions artifacts. It does not
publish a GitHub Release or run automatically when a tag is pushed.

## Signing setup

Create and safely back up a signing keystore. Never commit it or its passwords.

```bash
keytool -genkeypair -v -keystore chika-eink-release.jks -alias chika-eink \
  -keyalg RSA -keysize 2048 -validity 10000
# macOS/Linux: encode without platform-specific base64 flags.
base64 < chika-eink-release.jks > keystore.b64
```

Configure repository Actions secrets:

| Secret | Value |
| --- | --- |
| `KEYSTORE_BASE64` | Contents of `keystore.b64` |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Your signing alias |
| `KEY_PASSWORD` | Key password |

## Build a candidate

Create and push a tag on the intended commit, using `vMAJOR.MINOR.PATCH` with an
optional prerelease suffix, such as `v0.2.1-eink.1`. In Actions, run **Release
candidate**, entering that tag. The workflow validates the tag, checks out its
commit, runs both unit-test suites and lint, then builds the signed APK and AAB.
Branch names are not accepted as versions.

The artifact also includes the R8 mapping. Keep it with the matching candidate
for interpreting obfuscated crash reports. Version code retains the existing
`MAJOR*10000 + MINOR*100 + PATCH` calculation.

Local `./gradlew :app:assembleRelease` without signing environment variables
produces an unsigned release APK. Debug builds use the standard debug key.

For a signed local build, set these environment variables in your shell or through
a local secret manager before running the same command:

| Variable | Value |
| --- | --- |
| `KEYSTORE_FILE` | Absolute path to the existing release keystore |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Signing alias |
| `KEY_PASSWORD` | Key password |

Keep values outside tracked files and avoid printing them in build logs. With
those variables set, `./gradlew :app:assembleRelease` produces
`app/build/outputs/apk/release/app-release.apk`. Without them, the unsigned output
is `app/build/outputs/apk/release/app-release-unsigned.apk`.
`VERSION_NAME` optionally sets the release version; without it the current default
is `0.3.0` (version code `300`). GitHub release candidates use their selected tag's
version. No automatic version bump is performed.

The fork's release key is configured in repository Actions secrets. Its local
backup and credentials live in the ignored `.signing/` directory. Back up both
files securely outside this checkout: losing the key prevents signing updates
for installed releases. Do not generate a replacement key for each version.
A release signed with this key cannot update a debug-signed installation of the
same application ID. The fork now uses `io.github.sztkp.chikaeink` and can install
alongside upstream/old-ID builds; their app data is not automatically migrated.
The release certificate SHA-256 fingerprint is
`42e13fe3f6e0c62cc1073a9afaf34ed1ae20a7b594d535d7c3e672c3f954e792`.

## Distribution review

7-Zip-JBinding and CBR/RAR support have been removed. New builds contain no archive
library with the unRAR restriction; older draft assets are not changed by this removal.

Before public distribution, complete the model licensing/corresponding-source review,
smoke-test the minified release on a device, verify native-library alignment against
the chosen distribution channel's requirements, and publish matching source and
notices. This workflow does not establish licensing or store eligibility.
