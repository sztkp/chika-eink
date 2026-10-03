# Developing Kuro

## Setup and checks

Use the included Gradle wrapper with JDK 21. Install Android SDK packages
`platforms;android-36` and `build-tools;36.1.0`, accept their licenses, and set
`ANDROID_HOME` or `sdk.dir` in an untracked `local.properties`. Android Studio is optional.

Gradle's checked-in daemon criteria select JetBrains JDK 21 and may download it.
Both modules emit JVM 17 bytecode. On Windows, use `gradlew.bat` and set `JAVA_HOME`
if Java is not on PATH.

```bash
./gradlew :shared:jvmTest :app:testDebugUnitTest :app:lintDebug
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

CI runs the tests, lint, and debug build; lint errors fail CI.
The lint report is `app/build/reports/lint-results-debug.html`.

## Architecture

- `:shared`: JVM-targeted Kotlin core. Archive interfaces, natural page ordering,
  panel geometry, detection post-processing, reading-order planning, and framing
  math live here. Keep Android APIs out of `commonMain`.
- `:app`: Android/Compose UI, Room persistence, preferences, archive I/O, bitmap
  loading, and LiteRT inference. Platform integrations implement the core interfaces.

The installed application ID is `io.github.sztkp.chikaeink`; the Android namespace
and Kotlin/Java packages are `com.chakra.comicreader`. These and the legacy
`chika.settings` preference store are retained for install/data compatibility;
renaming the product does not migrate Android identity.

## Boundaries and invariants

- Imports copy archives into app-private storage; Room stores library metadata and
  page/panel progress. Covers are generated thumbnails. Backup rules are documented
  in [PRIVACY.md](../PRIVACY.md).
- Archive routing uses container signatures before extension fallback. Only ZIP
  containers are readable; RAR signatures produce conversion guidance, including
  when the file has a misleading extension.
- Android prepares detector input and runs inference; shared code decodes normalized
  page coordinates and plans regions in LTR/RTL order. Missing or unreliable
  detections fall back to full-page reading.
- Reader state owns navigation and persisted progress; Compose renders that state.
  Each page is a full-page view, its planned regions, then a full-page view before
  the next page. Page jumps land on the full-page view.
- Android e-ink controllers own firmware calls, the Compose-host View bridge, and
  lifecycle restoration. The reader signals settled images and interaction boundaries;
  refresh policy is independent of vendor APIs and persistent reading state.
- Bitmap loading and panel detection are cached and prefetched. Archive decoding
  and inference are serialized to protect shared resources.

Experimental [training tools](../training/README.md) are separate from the Android
build and do not reproduce the bundled model automatically.

## Assets and notices

The build copies authoritative legal files from the repository into `assets/legal/`.
Update [component notices](../THIRD_PARTY_NOTICES.md) and
[license provenance](../THIRD_PARTY_LICENSES/README.md) when changing dependencies
or assets. See the [licensing audit](LICENSING.md) for distribution review,
[artwork credits](../THIRD_PARTY_NOTICES.md#branding-and-artwork), and
[e-ink notes](EINK.md) for display/input constraints.
