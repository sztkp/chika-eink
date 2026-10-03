# Artwork provenance — Chika-eInk

This independent fork credits [Chika](https://github.com/batunii/chika) by Chakra
(Chalchitra Krida). Upstream's notices exclude its Chika / Chitra Katha logo and
wordmark from the source-code license. No separate permission for the promotional
screenshots or their depicted comic artwork is documented in this repository.

## Removed or replaced

- Removed `docs/logo-lockup.png`, `docs/screens/library.png`, `docs/screens/reader.png`,
  and both inherited fastlane phone screenshots.
- Replaced Android launcher artwork (vector and PNGs) and the fastlane icon with
  original monochrome open-book geometry.
- Replaced the Android graphical wordmark with plain “Chika-eInk” text.

The icon uses two simple polygonal pages, created for this fork without upstream image
inputs. Its vector, generated PNGs, and generator are distributed under [MPL-2.0](../LICENSE).
Run `python3 tools/generate_fork_icons.py` from the repository root to regenerate the PNGs
(requires Pillow). Existing asset names are retained to avoid changing resource references.

## Retained

- Libron v0.25 desktop fonts, with their SIL Open Font License and upstream copyright notices.
- The model bytes remain unchanged; corrected AGPL-3.0 and training-data notices are recorded in
  [the licensing audit](LICENSING.md).
- Upstream author attribution and all existing license texts.

## Fork screenshots

The current `docs/screens/{library,reader,panel,menu,sorting,actions,progress}.png`
are unedited captures of the revised UI (source commit `613233b`), refreshed on
2026-10-03. They run on an Android API 37 emulator configured at 824 × 1648,
300dpi, and font scale 0.85, matching the connected Palma 2's reported UI settings.
The emulator's Android version differs from that device's Android 13. They show Libron
typography and the original “Quiet Library Demo” comic, made from simple geometric
drawings for this fork. These replace the removed upstream screenshots; they are
not photographs of a BOOX device and do not demonstrate physical e-ink refresh.

The demo artwork, screenshot captures, and generator are distributed under
[MPL-2.0](../LICENSE); Libron retains its OFL and copyright notices. No external
comic artwork was used. Generate the three-page CBZ with
`python3 tools/generate_screenshot_comic.py /tmp/chika-demo` (requires Pillow),
import it using the app's file picker, and capture the screen using
`adb exec-out screencap -p > screenshot.png`.

User-imported comic pages/covers are not distributed as repository artwork. Future
screenshots should use original synthetic art or artwork with documented redistribution
permission and attribution. The cleanup removes images from the current tree; prior
commits retain their original contents. Git history has not been rewritten.
