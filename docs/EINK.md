# E-ink behaviour

## Display design

The interface uses opaque black and white, clear borders, Libron typography,
static loading indicators, and no click ripples. Panel framing, page changes,
control visibility, and zoom reset apply immediately. The comic action menu
retains Material's brief built-in transition.

Artwork fits between the visible controls and above the Progress panel; hiding
controls expands the viewport. Android system bars are hidden in the reader and
can be revealed by an edge swipe. A static dotted outline marks the whole image
in panel view, with offscreen edges left offscreen.

The Progress slider previews a page number while dragging and loads that page on
release. Done or Back closes the panel.

## Hardware buttons

Volume/Page Down advances; Volume/Page Up goes back in either reading direction.
Standard key presses act once; Android repeat events are consumed without navigation
or volume changes. Physical button holds can still repeat navigation on Palma 2;
BOOX firmware remapping and the delivered events need further diagnosis.
While Progress is open, the buttons navigate whole pages. Outside the reader,
Android handles them normally. Back, power, and mute are not intercepted.

The app uses standard Android key events; no vendor key API is used.
Physical button mapping depends on the device and BOOX button configuration.

## BOOX refresh integration

Palma 2 is the primary hardware target. Onyx/BOOX manufacturer or brand markers
select an Android-only controller using a small reflection bridge to firmware
View APIs. No Onyx SDK is bundled. These undocumented APIs may be absent or blocked;
unsupported firmware and ordinary Android devices use normal Android rendering.
Palma 2 firmware `4.2-rel_05132_72a2c1b9e` blocks constant lookup but exposes View
hooks. An exact model/Android/build profile supplies verified waveform identifiers
for that firmware only; a firmware update may therefore require revalidation.

While the reader is active, its Compose host View uses REGAL through firmware
discovery or the verified profile, otherwise GU. Distinct settled page/panel images
request one partial
refresh after drawing; unrelated recompositions do not request refreshes.
GC clears ghosting after five displayed page changes, counting forward/backward
navigation and jumps, but not panels. The counter is session-only. Firmware without
GC support retains partial refreshes. A manual full-refresh operation is available
to reader code; no new menu control is exposed.

Pinch/pan keeps the reading mode and requests a quality refresh when interaction
ends. No A2/fast mode is forced: its benefit and reliable switching need device
testing. Reader exit, Activity pause, and disposal cancel pending requests and
restore the previous View mode where firmware permits. Library/settings screens
keep system defaults; application-wide modes are never changed.

## Limitations and device checks

On Palma 2, firmware calls, partial requests, GC after five page changes, and
injected Android key handling have been exercised without API errors. Comparative
waveform quality remains unverified. Check ghosting and latency with line art,
screentones, and grayscale scans, rapid panel taps, pinch/pan, and physical
side-button mapping. Other BOOX firmware and devices remain untested.

No dithering or grayscale conversion is applied; imported images are preserved.
Accessibility testing, including TalkBack and enlarged fonts, remains outstanding.
