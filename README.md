# Kuro

Kuro is an independent Android fork of [Chika](https://github.com/batunii/chika), the
panel-by-panel comic reader by Chakra (Chalchitra Krida), adapted for e-ink.
Initially targeting **BOOX Palma 2**, with a high-contrast monochrome interface,
static indicators, and immediate panel transitions.

[CI](https://github.com/sztkp/kuro/actions/workflows/ci.yml) ·
[Issues](https://github.com/sztkp/kuro/issues) · Android 8.0+

## Features

- **CBZ/ZIP reading** with offline, on-device panel detection.
- LTR/RTL reading, tap and hardware-button navigation, pinch zoom, pan,
  whole-page view, and page jumping.
- BOOX reader refresh integration, with safe fallback on other Android devices.
- Library sorting, saved page/panel progress, and confirmed remove/reset actions.
- No accounts, ads, analytics, or app network access. [Privacy policy](PRIVACY.md).

CBR/RAR is unsupported. Extract its images and ZIP them into a CBZ to read it;
renaming the extension alone does not convert the archive.
See [e-ink notes](docs/EINK.md) for button behaviour and display limitations.

## Screenshots

| Library | Full page view | Panel view |
| --- | --- | --- |
| <img src="docs/screens/library.png" alt="Library with sort action and bottom-right add button" width="220"> | <img src="docs/screens/reader.png" alt="Full page view with compact controls" width="220"> | <img src="docs/screens/panel.png" alt="Panel view with dotted media outline" width="220"> |

## Install and build

Download the signed APK from [GitHub Releases](https://github.com/sztkp/kuro/releases)
and install it on your device. The fork installs alongside upstream Chika;
upstream app data is not migrated. Debug and release builds use different signing
keys, so a release cannot update a debug installation directly.

Use JDK 21 and Android SDK platform 36 / build tools 36.1.0:

```bash
./gradlew :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.
[Contributor setup and checks](docs/DEVELOPMENT.md) · [Release procedure](RELEASING.md).

## License

The combined app is distributed under [AGPL-3.0](DISTRIBUTION_LICENSE.md);
application source also retains [MPL-2.0](LICENSE). Bundled components have separate
[notices](THIRD_PARTY_NOTICES.md). Model source compliance remains
[under review](docs/LICENSING.md).

## Acknowledgements

Chika by Chakra (Chalchitra Krida), [panel detector](https://huggingface.co/leoxs22/manga-panel-detector-yolo26n)
by Leandro Narosky, and [Libron](https://github.com/nicoverbruggen/libron) by Nico Verbruggen.
Kuro is an AI-assisted project developed with OpenAI Codex.
