# Artwork provenance — Chika-eInk

This independent fork credits [Chika](https://github.com/batunii/chika) by Chakra
(Chalchitra Krida). Upstream's notices exclude its Chika / Chitra Katha logo and
wordmark from the source-code license. No separate permission for the promotional
screenshots or their depicted comic artwork is documented in this repository.

## Removed or replaced

- Removed `docs/logo-lockup.png`, `docs/screens/library.png`, `docs/screens/reader.png`,
  and both inherited fastlane phone screenshots.
- Replaced Android launcher artwork (vector and PNGs), the fastlane icon, and the iOS
  app icon/mark with original monochrome open-book geometry.
- Replaced the Android and iOS graphical wordmark with plain “Chika-eInk” text.

The icon uses two simple polygonal pages, created for this fork without upstream image
inputs. Its vector, generated PNGs, and generator are distributed under [MPL-2.0](../LICENSE).
Run `python3 tools/generate_fork_icons.py` from the repository root to regenerate the PNGs
(requires Pillow). Existing asset names are retained to avoid changing resource references.

## Retained

- Anton and Archivo fonts, with their existing SIL Open Font License notices.
- The model and its inherited licensing/training-data disclosures, unchanged.
- `iosApp/ci/SyntheticManga.cbz`, the generated synthetic comic used by the existing CI.
- Upstream author attribution and all existing license texts.

User-imported comic pages/covers are not distributed as repository artwork. Future
screenshots should use original synthetic art or artwork with documented redistribution
permission and attribution. The cleanup removes images from the current tree; prior
commits retain their original contents. Git history has not been rewritten.
