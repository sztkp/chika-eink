# Developing Chika-eInk

## Build setup

Use the included Gradle wrapper with JDK 21 and the Android SDK. Android Studio
is optional; command-line SDK tools are sufficient. Install `platforms;android-36`
and `build-tools;36.1.0`, accept the SDK licenses, and set `ANDROID_HOME` or an
`sdk.dir` entry in an untracked `local.properties` file.

The checked-in daemon criteria select JetBrains JDK 21. Gradle may download that
runtime if it is not installed. The app and shared module emit JVM 17 bytecode;
that bytecode target is separate from the JDK running Gradle.

```bash
./gradlew :shared:jvmTest :app:testDebugUnitTest :app:lintDebug
./gradlew :app:assembleDebug
# Install on a connected Android device or emulator:
./gradlew :app:installDebug
```

On Windows, use `gradlew.bat` and set `JAVA_HOME` to your JDK directory if Java is
not on PATH. Android Studio's bundled JDK is another option.

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
Lint report: `app/build/reports/lint-results-debug.html`.

CI runs both unit-test suites, Android lint, and the debug build. Lint errors fail
CI; dependency/toolchain and target-SDK upgrade suggestions remain visible.
Changes to those pinned versions need separate compatibility testing.

## Modules

- `:shared`: pure Kotlin core targeting JVM, retaining `commonMain` / `commonTest`.
  Archive interfaces, page ordering, panel geometry, detection post-processing,
  panel planning, and camera/framing math live here.
- `:app`: Android entry points (`ComicReaderApp`, `MainActivity`), Compose navigation
  and screens, Room storage, preferences, archive implementations, bitmap loading,
  and LiteRT inference.

The app remains Android-only. Its installed application ID is
`io.github.sztkp.chikaeink`; the namespace and Kotlin/Java source packages remain
`com.chakra.comicreader`. The fork installs separately from upstream Chika and
old-ID fork builds, with independent app data and Android backups.

## Panel reading

1. Android inference letterboxes the image and passes it to the bundled detector.
2. The shared decoder filters detections and maps boxes back to page coordinates.
3. The existing panel pipeline orders regions for LTR/RTL reading and merges or
   divides regions for usable framing.
4. `ReaderViewModel` selects the current page/panel; `ReaderScreen` draws its camera
   immediately using the existing framing math. Failed detection falls back to
   full-page reading.

Detection, geometry, ordering, planning, and framing are inherited functionality.
Experimental [training tools](../training/README.md) are optional and are not part
of the Android build. They do not reproduce the bundled model automatically.

## Assets and notices

- [Artwork credits](../THIRD_PARTY_NOTICES.md#branding-and-artwork): fork icon and screenshots.
- [Licensing audit](LICENSING.md): unresolved model/native-library distribution review.
- [E-ink notes](EINK.md): implemented adaptations and device-testing limits.

`syncLegalAssets` copies the repository's authoritative notices into APK assets.
Keep the notices accurate when changing bundled components.
