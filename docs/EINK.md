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
The reader uses a compact 48dp control row with Android's standard back arrow and
a narrow page/panel count row. Artwork is framed in the space between these rows,
so controls do not cover it. Hiding controls expands the artwork viewport immediately.
System bars stay hidden during reading, remain available by edge swipe, and return
when leaving the reader.
A Progress action in the count row opens a Material dialog with a discrete page
slider and previous/next-page buttons. Slider release loads the selected whole
page; dragging only updates the preview count, avoiding repeated decoding during
scrubbing. The permanent reader bar still has no progress track.
Panel view has a static dotted black outline around the entire displayed comic image,
with white backing for contrast over dark artwork. It follows the image through pan/zoom and is
hidden in full-page view; detection and framing calculations are unchanged.
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

## Palma 2 hardware buttons

The reader handles standard Android Volume Down/Page Down as next panel and
Volume Up/Page Up as previous panel, through the existing reader state machine.
Buttons follow reading sequence in both LTR and RTL; they do not simulate swipes
or skip panels. Each press advances once; release and repeat events are consumed.
Outside the reader, Android handles these keys normally. Back, power and mute
are not intercepted. No BOOX SDK is needed.

If BOOX remaps a button to a simulated scroll gesture, configure that app's side
buttons to send volume/page key events instead. Actual availability depends on
firmware. No firmware settings are changed by this app.

The connected Palma 2 reports Android 13 and firmware 4.2, with Volume Up/Down
input capabilities. Automated regression tests cover both key families, held-key
behavior and unrelated keys. Physical-button mapping and ghosting still need
device testing; refresh/dithering behavior is unchanged.
ADB-injected Volume Down/Up events were verified in the reader on the device:
Down changed panel 0 to 1, and Up restored panel 0. The later Page Down/Up
check encountered the library instead of the reader, so device verification of
those events remains pending; their mapping is covered by unit tests.

## Android menu conventions

The library and About screens use Material 3 `Scaffold` and `TopAppBar`, with
content insets consumed below the app bar. About uses standard `ListItem`,
`Button` and `OutlinedButton` controls. Actions have button semantics and at
least 48dp layout height; headings and library actions have accessibility labels.
The library grid adapts to available width and uses Material typography for titles
and progress text. Monochrome colors, Libron and disabled ripple/navigation
animations remain intentional e-ink adaptations.

Library sorting is an app-bar action with an anchored menu of Material
`DropdownMenuItem` controls. The chosen order has a checkmark and selected semantics.
A focusable Android `Popup` preserves dismissal by Back/outside tap without the
scale/fade animation built into this version of `DropdownMenu`.

Import uses a standard Material 3 + floating action button in Scaffold's bottom-end
slot, above the system navigation inset. Grid padding and snackbar placement keep
content reachable around it. Elevation remains zero for e-ink, and importing uses
static status text while preventing duplicate picker launches.

Comic long-press actions use Material 3 `DropdownMenu` with `DropdownMenuItem`,
standard anchor positioning/dismissal and long-press haptic feedback. Remove and
reset still require separate confirmation dialogs. Unlike the static toolbar
sort popup, this standard component retains its built-in short menu transition;
monochrome colors, zero elevation and disabled ripples remain e-ink adaptations.

References: [app bars](https://developer.android.com/develop/ui/compose/components/app-bars),
[Scaffold](https://developer.android.com/develop/ui/compose/components/scaffold),
and [accessibility defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults).
Build and shared tests passed, and both screens were visually checked on the
connected Palma 2. TalkBack and enlarged-font testing remain necessary before
claiming comprehensive accessibility compliance.
