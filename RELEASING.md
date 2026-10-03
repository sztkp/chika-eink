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

Before public distribution, complete the model/native-library review, smoke-test
the minified release on a device, verify native-library alignment against the
chosen distribution channel's requirements, and publish matching source and
notices. This workflow does not establish licensing or store eligibility.
