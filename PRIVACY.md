# Kuro Privacy Policy

_Last updated: 3 October 2026_

Kuro is an independent Android fork of [Chika](https://github.com/batunii/chika),
a comic reader that runs entirely on your device.

## Data we collect

**No data is collected by the project or transmitted by the app.** Imported comics,
reading progress, library metadata, and settings are stored locally on your device.

- Kuro has **no network access**. The Android app declares no `INTERNET` permission. The app itself does not transmit reading data.
- Comics you import are copied into the app's private storage on your device and are readable by the app.
- Reading progress, library metadata, and settings are stored locally. The database
  may be backed up by Android; imported files, covers, and preferences are excluded
  by the current backup rules.
- Panel detection runs entirely on-device using a bundled machine-learning model. The app does not transmit page images or detector output.
- Kuro contains no analytics, no crash reporting, no advertising, and no third-party tracking SDKs.

## Data sharing

We share no data with anyone, because we have none.

## Data deletion

Uninstalling the app deletes its local data: imported comic copies, covers, reading progress, and settings.

## Android backups

The manifest allows system-managed backups of the library database. Whether that
database is backed up or restored depends on Android, device settings, and the
backup provider. The current backup and device-transfer rules exclude comic files,
covers, and preferences.
The app itself does not upload comic pages or reading data.

## External links

The About screen links to the fork, upstream project, privacy policy, and notices.
Opening a link hands off to your browser; the destination site's privacy terms apply.

## Children

Kuro does not collect data from anyone, including children.

## Changes

Any change to this policy will be published at this URL alongside the app's source code.

## Contact

Questions? Open an issue at <https://github.com/sztkp/chika-eink/issues>.
