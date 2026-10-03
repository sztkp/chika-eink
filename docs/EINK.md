# Chika-eInk first pass

The Android presentation uses opaque monochrome UI colors and the bundled Libron font family. The existing library,
reader layouts remain; the library info button opens an About section. Navigation, panel framing, page changes, chrome
visibility and double-tap zoom reset no longer animate. Loading/import status is
static; click ripples, snackbar transitions, halftone washes, decorative shadows
and reader gradients/reticles are removed from the displayed UI.

The theme toggle and its runtime setting have been removed. The reader canvas,
library and About section stay white with black controls and text.
Original comic images and covers remain unprocessed.

Panel selection draws the existing `currentCamera` directly with the unchanged
`computePageDraw` math. The existing 220 ms double-tap recognition window remains.
Pinch, pan, double-tap, tap zones and page flicks remain available. The reader shows
plain page/panel counts; its progress slider and redundant title subtitle were removed.
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
- Check tap response, double-tap reset, pinch/pan, flicks and page/panel counts.
- Check text, borders, image detail and system bars.
- Observe ghosting after repeated panel changes and chrome toggles; measure refresh
  latency using the device's existing modes before deciding whether vendor APIs help.
- Compare full versus partial refresh and refresh-after-navigation strategies on
  the physical device. None are implemented here.
- Compare representative line art, screentones and grayscale/color scans before
  considering dithering or grayscale processing. Keep processing separate from the
  unchanged ML input and detection pipeline if device evidence warrants a later change.

No BOOX APIs, forced refresh calls, dithering or grayscale preprocessing are added.

## Android menu conventions

The library and About screens use Material 3 `Scaffold` and `TopAppBar`, with
content insets consumed below the app bar. About uses standard `ListItem`,
`Button` and `OutlinedButton` controls. Actions have button semantics and at
least 48dp layout height; headings and library actions have accessibility labels.
The library grid adapts to available width and uses Material typography for titles
and progress text. Monochrome colors, Libron and disabled ripple/navigation
animations remain intentional e-ink adaptations.

References: [app bars](https://developer.android.com/develop/ui/compose/components/app-bars),
[Scaffold](https://developer.android.com/develop/ui/compose/components/scaffold),
and [accessibility defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults).
Build and shared tests passed, and both screens were visually checked on the
connected Palma 2. TalkBack and enlarged-font testing remain necessary before
claiming comprehensive accessibility compliance.
