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

The bundled archive library version is `Release-16.02-2.03`. Record and provide the
applicable corresponding library source and build materials, and verify a usable
replacement/relinking route for the distributed build under LGPL §6. A source link and
a dynamically loaded `.so` are evidence about packaging, not a completed compliance check.
Retain any additional native-component notices found during that source review.

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
