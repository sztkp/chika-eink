# Chika-eInk first pass

The Android presentation uses opaque monochrome UI colors and the bundled Libron font family. The existing library,
menu and reader layouts remain. Navigation, panel framing, page changes, chrome
visibility and double-tap zoom reset no longer animate. Loading/import status is
static; click ripples, snackbar transitions, halftone washes, decorative shadows
and reader gradients/reticles are removed from the displayed UI.

The saved AMOLED setting remains under “Black reader background”. It controls only
the reader canvas; library, menu and reader controls stay white with black text.
Original comic images and covers remain unprocessed.

Panel selection draws the existing `currentCamera` directly with the unchanged
`computePageDraw` math. The existing 220 ms double-tap recognition window remains.
Pinch, pan, double-tap, tap zones, page flicks and the scrubber remain available.
No dependencies, detection/ML, panel planning/order, archives, persistence,
page decoding or caches are changed. The fork targets Android only; the core module
retains its existing source layout with a JVM target.

## Validation

Run `./gradlew :shared:jvmTest :app:testDebugUnitTest :app:lintDebug`, then
`./gradlew :app:assembleDebug`. The APK is at
`app/build/outputs/apk/debug/app-debug.apk`.

Android lint currently reports the upstream base-theme
`windowLayoutInDisplayCutoutMode` attribute as requiring API 27 while minSdk is 26.
This first pass leaves that compatibility issue and dependency/deprecation
warnings unchanged.

## Physical Palma 2 checks and deferred work

- Import CBZ/CBR, reopen at saved progress, and verify library deletion.
- Read in LTR/RTL through full-page intro/outro slots and page boundaries.
- Check tap response, double-tap reset, pinch/pan, flicks and page scrubber.
- Check text, borders, image detail, system bars and the optional black canvas.
- Observe ghosting after repeated panel changes and chrome toggles; measure refresh
  latency using the device's existing modes before deciding whether vendor APIs help.
- Compare full versus partial refresh and refresh-after-navigation strategies on
  the physical device. None are implemented here.
- Compare representative line art, screentones and grayscale/color scans before
  considering dithering or grayscale processing. Keep processing separate from the
  unchanged ML input and detection pipeline if device evidence warrants a later change.

No BOOX APIs, forced refresh calls, dithering or grayscale preprocessing are added.
