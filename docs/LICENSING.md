# Licensing audit — 3 October 2026

**Status: documentation corrected; distribution clearance remains open.**
Application source retains MPL-2.0. This audit does not apply a new license to the
combined app/model or replace any detection code or weights.

## Findings corrected

- README and notices now identify Libron v0.25, OFL-1.1, and its Readerly/Newsreader origins.
  All four font files were checked byte-for-byte against the official desktop release.
- Added Apache Commons Compress/Codec/IO/Lang artifact LICENSE and NOTICE files.
- Replaced the stale TensorFlow Lite coordinate with the actual LiteRT 1.4.2 dependencies;
  retained the AARs' license texts, including Caffe's BSD attribution.
- Added pinned 7-Zip/p7zip, unRAR, LZHAM, and JBinding notices/attribution. Removed the
  unsupported claim that dynamic loading alone establishes LGPL compliance.
- Corrected the bundled model's license from Apache-2.0 to AGPL-3.0, following the
  author's September 2026 correction. Removed the blanket compatibility/clearance claim.
- Checked runtime dependency metadata and local artifacts, not just direct Gradle entries.
  Native transitive source/license completeness is still a release-review item.
- Build configuration packages the notices and license texts in `assets/legal/`, without
  requiring duplicated manually maintained asset copies.

## Model identity and remaining decision

- File: `app/src/main/assets/manga_panel_detector_int8.tflite`.
- SHA-256: `b1a7d8d4492e04a777ae0d3efd9dc1fbd6e8f361971eadb813279ce3dfd1b464`.
- Matches the model repository's LFS hash at observed revision
  `40a2854663d537563cfb95c370288a84c6505b9a`.
- [Pinned upstream model card](https://huggingface.co/leoxs22/manga-panel-detector-yolo26n/blob/40a2854663d537563cfb95c370288a84c6505b9a/README.md).
- [Dataset owner's Manga109-s terms](https://huggingface.co/datasets/hal-utokyo/Manga109-s).

Before treating a model-bearing distribution as cleared, select and validate a route:
retain the model with a suitable copyleft/source-compliance approach, obtain sufficient
alternative permission, or replace it with a model whose rights fit the intended
licensing. An old Apache label is not evidence of permission: the author explicitly
says that grant was mistaken. Merely adding the AGPL text does not resolve the issue.

## Native-library release review

The bundled archive library version is `Release-16.02-2.03`, from commit
[`875f38aac441f41e6eb693177e020e97971dca97`](https://github.com/omicronapps/7-Zip-JBinding-4Android/tree/875f38aac441f41e6eb693177e020e97971dca97).
Its source tree includes the Java binding, native sources and build scripts. The
app accepts `-PsevenZipAar=/absolute/path/to/replacement.aar` to rebuild with a
compatible modified library; the default dependency remains unchanged. See
[release instructions](../RELEASING.md#rebuilding-with-a-modified-archive-library).

### Copyleft compatibility blocker

The archive library includes RAR decompression code under LGPL **plus the unRAR
restriction**, which prohibits recreating the RAR compression algorithm. This
additional restriction needs resolution before relying on an AGPL/GPL combined-work
distribution route for the app and bundled detector. Providing source and license
texts does not remove it. The [7-Zip notice](../THIRD_PARTY_LICENSES/SevenZip-NOTICE.txt)
records the restriction; the [GNU compatibility guidance](https://www.gnu.org/licenses/gpl-faq.html#GPLIncompatibleLibs)
explains that exceptions require permission from the relevant copyright holders.

The release candidate stays private pending sufficient permission or another
validated licensing route. RAR support and the detector have not been removed or
replaced to work around this issue.

## Release source materials

The private v0.3.0 candidate includes the detector's editable FP32 checkpoint from
the pinned model revision above, SHA-256
`73e0fb587ea3afe0d17aa9f0c3b1f5a8001b3ecbc3c77091e0730654b0da9146`.
Checkpoint metadata identifies Ultralytics 8.4.31, source commit
[`65b736045f7e8d54bbf3fd27709f4b1321b3b532`](https://github.com/ultralytics/ultralytics/tree/65b736045f7e8d54bbf3fd27709f4b1321b3b532).
These source archives, the model card and retained licenses are collected with the
candidate. The Manga109-s training/calibration dataset is not redistributed.
Exact INT8 export reproducibility and complete corresponding-source obligations
remain review items; collecting these materials is not a clearance claim.

The root MPL text is preserved, including its secondary-license provisions. No
combined-work license choice is made by this documentation update.

## Rechecking after changes

1. Resolve runtime graphs with `./gradlew :app:dependencies --configuration debugRuntimeClasspath`
   and `releaseRuntimeClasspath` when preparing a release; build-time tools are separate.
2. Check new artifacts' POMs, embedded LICENSE/NOTICE files, and native subcomponents.
3. Update retained texts, [the notice inventory](../THIRD_PARTY_NOTICES.md), and
   [text provenance](../THIRD_PARTY_LICENSES/README.md).
4. Build the APK and verify `assets/legal/` contains the source notice, font attribution,
   component notices, and applicable license texts.
5. Resolve the model and native-library items before making a distribution-clearance claim.

Previous binaries and Git history are not retroactively altered by these corrections.
