# Chika-eInk

An independent Android fork of [Chika](https://github.com/batunii/chika), the
panel-by-panel comic reader by Chakra (Chalchitra Krida), adapted for e-ink.
The initial target is the **BOOX Palma 2**. Maintained at
[sztkp/chika-eink](https://github.com/sztkp/chika-eink); this is not an official upstream release.

[CI](https://github.com/sztkp/chika-eink/actions/workflows/ci.yml) · Android 8.0+

## Reading on e-ink

Open a CBZ or CBR archive and tap through its detected panels in reading order.
Panel framing and page changes are immediate. The interface uses opaque black and
white, clear borders, static status indicators, and Libron typography.

- **Comic support:** CBZ, CBR, and RAR5; offline, on-device panel detection.
- **Reader:** LTR/RTL, tap navigation, pinch zoom, pan, whole-page view, and a
  compact Progress panel for jumping between pages.
- **Hardware buttons:** Volume/Page Down advances; Volume/Page Up goes back.
  Buttons navigate whole pages while Progress is open. Holding a button does not
  repeatedly advance.
- **Library:** saved page/panel progress, sorting by title, last added, or last read,
  and long-press actions to remove a comic or reset its progress with confirmation.
- **Privacy:** no accounts, ads, analytics, or app network permission.
  See [the privacy policy](PRIVACY.md) for local storage and Android backups.

Original comic images are preserved. No BOOX-specific refresh APIs, dithering, or
image preprocessing are implemented. See [e-ink notes](docs/EINK.md) for device
validation and refresh work that still needs physical testing.

## Screenshots

Actual emulator captures at the connected Palma 2's reported resolution, density,
and font scale. The original demo manga **六本木深夜決戦** stages a midnight showdown
over the last onigiri at a Roppongi 7-Eleven, with kanji/katakana lettering and RTL reading.

| Library | Full page view | Panel view |
| --- | --- | --- |
| <img src="docs/screens/library.png" alt="Library with sort action and bottom-right add button" width="220"> | <img src="docs/screens/reader.png" alt="Full page view with compact controls" width="220"> | <img src="docs/screens/panel.png" alt="Panel view with dotted media outline" width="220"> |

These captures do not simulate physical e-ink refresh.

## Build

Use JDK 21 and the Android SDK (platform 36, build tools 36.1.0).
**Android Studio is optional.** Use the included Gradle wrapper:

```bash
./gradlew :shared:jvmTest :app:testDebugUnitTest :app:lintDebug
./gradlew :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.
[Setup and architecture](docs/DEVELOPMENT.md) · [Distribution status](DISTRIBUTION.md) ·
[Release candidates](RELEASING.md).

## License

Application source retains [MPL-2.0](LICENSE). Bundled components have separate
terms: Libron is OFL-1.1 and the detector weights are declared AGPL-3.0 upstream.
Notices and license texts are included in APKs under `assets/legal/`.

**Distribution review remains open** for the model and native-library source and
replacement obligations. See [the licensing audit](docs/LICENSING.md) and
[third-party notices](THIRD_PARTY_NOTICES.md). The MPL source license does not
clear the combined APK for distribution.

## Acknowledgements

- Original application: [Chika](https://github.com/batunii/chika), by Chakra (Chalchitra Krida).
- **OpenAI Codex** — Chika-eInk is an AI-assisted project, with Codex used to help implement changes, update documentation, and validate builds.
- Panel-detection model: [`leoxs22/manga-panel-detector-yolo26n`](https://huggingface.co/leoxs22/manga-panel-detector-yolo26n) (upstream AGPL-3.0 declaration), trained on Manga109-s.
- UI font: [**Libron**](https://github.com/nicoverbruggen/libron) by Nico Verbruggen, derived from Readerly and Newsreader (SIL Open Font License 1.1).
- **LiteRT**, **Apache Commons Compress**, **7-Zip-JBinding-4Android**, and the AndroidX/Kotlin contributors.
