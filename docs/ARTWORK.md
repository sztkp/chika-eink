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

The README's `docs/screens/{library,reader,panel}.png` are unedited captures of
source commit `613233b`, refreshed on 2026-10-03. They run on an Android API 37
emulator configured at 824 × 1648, 300dpi, and font scale 0.85, matching the
connected Palma 2's reported UI settings. The emulator's Android version differs
from that device's Android 13. These are not photographs of a BOOX device and do
not demonstrate physical e-ink refresh.

They show the original one-page demo manga **六本木深夜決戦** (Roppongi Midnight
Showdown): a tongue-in-cheek encounter over the last onigiri at a Roppongi
7-Eleven. Its title and dialogue use kanji and katakana, without English or
hiragana. The reader is set to RTL for the manga screenshots. The app's existing
Libron typography uses Android's fallback for Japanese characters.

The page at `docs/demo/roppongi.png` was generated using Codex's built-in image
generation tool, without reference images or external comic artwork. The generation
prompt is recorded in [docs/demo/PROMPT.md](demo/PROMPT.md). The fork contributes
its artwork and captures under [MPL-2.0](../LICENSE) to the extent it holds rights;
AI-generated material may not itself qualify for copyright in every jurisdiction.
7-Eleven's name and identifying store references remain third-party trademarks;
this fictional demo does not imply endorsement or affiliation.

Package the demo CBZ with
`python3 tools/generate_screenshot_comic.py /tmp/chika-demo` (Python standard
library only), import it using the app's file picker, and capture the screen using
`adb exec-out screencap -p > screenshot.png`. The script packages the committed
page unchanged; it does not regenerate AI artwork.

The additional `docs/screens/{menu,sorting,actions,progress}.png` captures are
retained from the earlier UI documentation and are not embedded in the README.
They show the geometric “Quiet Library Demo”, originally drawn for this fork
under MPL-2.0, using Libron under its OFL and copyright notices.

User-imported comic pages/covers are not distributed as repository artwork. Future
screenshots should use original synthetic art or artwork with documented redistribution
permission and attribution. The cleanup removes images from the current tree; prior
commits retain their original contents. Git history has not been rewritten.
