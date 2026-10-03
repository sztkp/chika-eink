# Chika-eInk adaptations

## Presentation and reader

The library, reader controls, and About screen use white backgrounds, black text
and icons, clear borders, and Libron typography. The theme toggle is removed.
Loading/import indicators are static. Decorative gradients, shadows, reticles,
ripples, and navigation transitions are removed from the displayed UI.

Panel framing, page changes, chrome visibility, and double-tap reset apply
immediately. The reader draws the existing `currentCamera` through unchanged
`computePageDraw` math. The 220 ms double-tap recognition window, tap zones, pinch,
pan, and page flicks remain available.

Compact top and bottom rows frame artwork in the space between them. Hiding the
controls expands the viewport. Android system bars stay hidden in the reader,
can be revealed by edge swipe, and return when leaving it. Panel view draws a
static dotted outline around the entire transformed image, rather than its
selected panel; offscreen edges stay offscreen.

Progress opens an outlined bottom panel with a discrete page slider and
previous/next-page buttons. Artwork fits above it. Dragging updates the preview
count; releasing the slider loads the selected page. Done or Back closes it.

Original images and covers are unprocessed. Dependency versions, detector/model,
panel geometry/order/planning, framing calculations, archives, and page decoding
are retained. Sorting preferences and progress-reset operations use the existing
persistence without a schema change.

## Hardware buttons

Volume/Page Down advances and Volume/Page Up goes back through the existing
reader sequence in either LTR or RTL. Each press acts once; repeat and release
events are consumed. While Progress is open, these buttons navigate whole pages.
Outside the reader, Android handles them normally. Back, power, and mute are not
intercepted. No BOOX SDK or firmware-setting changes are involved.

The connected Palma 2 reports Android 13 and firmware 4.2. The user confirmed
physical volume-button navigation; injected Volume Down/Up events also advanced
and restored a panel. Unit tests cover both key families, repeats, and unrelated
keys. Physical Page Down/Up mapping remains unverified.

## Android UI conventions

Library and About use Material 3 Scaffold, TopAppBar, content insets, accessible
button targets, and heading/action semantics. Import uses a bottom-end + FAB;
static status text prevents duplicate imports. Sorting uses an anchored, focusable
Popup with Material menu items and selected-order semantics, avoiding the
standard dropdown's transition.

Comic long-press actions use a standard Material DropdownMenu with haptic feedback
and confirmation dialogs for removal/reset. This component retains its brief
built-in menu transition. Zero elevation and disabled ripples are intentional
e-ink adaptations. TalkBack and enlarged-font testing remain outstanding.

## Validation and deferred device testing

Both unit-test suites, Android lint, and the debug build pass. Lint's remaining
warnings suggest dependency/toolchain or SDK upgrades; pinned versions are kept.
The cutout attribute is API-qualified. See [development instructions](DEVELOPMENT.md).

Before adding refresh or image-processing behavior, test on the physical device:

- Reopen saved progress; read LTR/RTL through panel and page boundaries.
- Check gestures, whole-page jumps, physical key mapping, and system bars.
- Check text, borders, image detail, accessibility, and enlarged fonts.
- Measure ghosting and refresh latency across the device's existing refresh modes.
- Compare full/partial refresh and refresh-after-navigation strategies.
- Compare line art, screentones, grayscale, and color scans before considering
  dithering or grayscale processing; keep any processing separate from ML input.

No vendor refresh APIs, forced refresh calls, dithering, or grayscale preprocessing
are implemented. These require evidence from device testing.
