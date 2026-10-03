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
Each press acts once; holding a key does not repeat navigation or adjust volume.
While Progress is open, the buttons navigate whole pages. Outside the reader,
Android handles them normally. Back, power, and mute are not intercepted.

The app uses standard Android key events, without a BOOX SDK or firmware changes.
Physical Page Up/Down mapping depends on the device and remains unverified on Palma 2.

## Limitations and device checks

No vendor refresh APIs, forced refresh calls, dithering, or grayscale conversion
are implemented. Display quality and ghosting depend on the device's refresh mode.
Imported archive images are preserved; any future display processing should remain
separate from detector input.

Before changing refresh or rendering behaviour, validate on hardware:

- Panel/page boundaries, LTR/RTL order, gestures, saved progress, and key mapping.
- Ghosting and latency across refresh modes, using line art, screentones, grayscale,
  and colour scans.
- TalkBack, enlarged fonts, control targets, borders, and image detail.

Comprehensive refresh/ghosting and accessibility testing remain outstanding.
