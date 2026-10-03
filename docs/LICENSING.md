# Licensing audit — 3 October 2026

**Status: the unRAR compatibility conflict is removed from new builds; model distribution review remains open.**
Application source retains MPL-2.0. This audit does not apply a new license to the
combined app/model or replace any detection code or weights.

## Findings corrected

- README and notices now identify Libron v0.25, OFL-1.1, and its Readerly/Newsreader origins.
  All four font files were checked byte-for-byte against the official desktop release.
- Added Apache Commons Compress/Codec/IO/Lang artifact LICENSE and NOTICE files.
- Replaced the stale TensorFlow Lite coordinate with the actual LiteRT 1.4.2 dependencies;
  retained the AARs' license texts, including Caffe's BSD attribution.
- Removed 7-Zip-JBinding, its archive backend and bundled notices, and the library
  replacement build option. CBR/RAR is recognized for an unsupported-format message;
  only CBZ/ZIP is readable.
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

## Archive-library conflict resolved for new builds

The previous 7-Zip-JBinding dependency included LGPL code plus the unRAR restriction,
which conflicted with the proposed AGPL/GPL combined-work distribution route.
It has now been removed entirely, including its RAR reader, Gradle dependency,
replacement-AAR option, shrinker rules, and packaged license materials. New builds
read CBZ/ZIP with Apache Commons Compress and reject CBR/RAR with conversion guidance.
Merely renaming a RAR file to CBZ does not make it readable.

Existing RAR library records and files are left intact. Users must convert and
reimport them; reading progress is not automatically transferred. ZIP containers
remain readable even with a misleading extension.

The detector is unchanged. Removing the archive library resolves this specific
compatibility conflict; it does not itself select the combined-work license or
complete the model's corresponding-source obligations. LiteRT native components
still require their applicable notices and build/channel checks.

## Release source materials

The private v0.3.0 candidate includes the detector's editable FP32 checkpoint from
the pinned model revision above, SHA-256
`73e0fb587ea3afe0d17aa9f0c3b1f5a8001b3ecbc3c77091e0730654b0da9146`.
Checkpoint metadata identifies Ultralytics 8.4.31, source commit
[`65b736045f7e8d54bbf3fd27709f4b1321b3b532`](https://github.com/ultralytics/ultralytics/tree/65b736045f7e8d54bbf3fd27709f4b1321b3b532).
These source archives, the model card and retained licenses are collected with the
older candidate. Its archive-library source bundle and binaries are historical;
new release assets must be rebuilt from the updated source. The Manga109-s
training/calibration dataset is not redistributed.
Exact INT8 export reproducibility and complete corresponding-source obligations
remain review items; collecting these materials is not a clearance claim.

The root MPL text is preserved, including its secondary-license provisions. No
combined-work license choice is made by the archive-library removal.

## Rechecking after changes

1. Resolve runtime graphs with `./gradlew :app:dependencies --configuration debugRuntimeClasspath`
   and `releaseRuntimeClasspath` when preparing a release; build-time tools are separate.
2. Check new artifacts' POMs, embedded LICENSE/NOTICE files, and native subcomponents.
3. Update retained texts, [the notice inventory](../THIRD_PARTY_NOTICES.md), and
   [text provenance](../THIRD_PARTY_LICENSES/README.md).
4. Build the APK and verify `assets/legal/` contains the source notice, font attribution,
   component notices, and applicable license texts.
5. Resolve the model licensing and corresponding-source items before making a distribution-clearance claim.

Previous binaries and Git history are not retroactively altered by these corrections.
