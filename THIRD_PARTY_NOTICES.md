# Third-Party Notices — Chika-eInk

Reviewed 3 October 2026. Chika-eInk is an independent fork of
[Chika](https://github.com/batunii/chika) by Chakra (Chalchitra Krida).
Application source remains under [MPL-2.0](LICENSE); bundled components retain their
own licenses. Source: <https://github.com/sztkp/chika-eink>.

**Distribution review is incomplete.** The bundled model is AGPL-3.0, not Apache-2.0,
and native-library source/replacement obligations need verification. See
[the audit and remaining decisions](docs/LICENSING.md). Keeping notices does not by
itself establish compliance with every dependency's distribution terms.

## Component inventory

| Component | Version / scope | License / notice |
|---|---|---|
| Chika-eInk application source | including inherited Chika code | [MPL-2.0](LICENSE) |
| Kotlin standard library | 2.2.10 | Apache-2.0 |
| Kotlin coroutines | 1.9.0 | Apache-2.0 |
| Kotlin serialization | resolved 1.6.3 | Apache-2.0 |
| JetBrains annotations | resolved 23.0.0 | Apache-2.0 |
| AndroidX / Compose / Material Icons / Room | versions in Gradle catalog and resolved graph | Apache-2.0 |
| Guava listenablefuture | resolved 1.0 | Apache-2.0 |
| Apache Commons Compress | 1.27.1 | Apache-2.0; retained LICENSE and NOTICE |
| Apache Commons Codec / IO / Lang | resolved 1.17.1 / 2.16.1 / 3.16.0 | Apache-2.0; retained LICENSE and NOTICE |
| LiteRT and LiteRT API | `com.google.ai.edge.litert`, 1.4.2 | Apache-2.0 plus bundled Caffe BSD notice |
| 7-Zip-JBinding-4Android | `Release-16.02-2.03` | LGPL-2.1-or-later, unRAR restriction, bundled subcomponent notices |
| Libron desktop font family | v0.25, unmodified regular/bold/italic/bold-italic | OFL-1.1 |
| Panel detector weights | `manga_panel_detector_int8.tflite` | upstream declares AGPL-3.0; Manga109-s training disclosure below |
| Fork book icon | original vector and generated PNGs | MPL-2.0; [provenance](docs/ARTWORK.md) |

Build tools (Gradle, Android/Kotlin plugins, KSP) and experimental training tools are
not app runtime dependencies. Training tools and data still have their own terms.
The table groups AndroidX modules; it is not an exhaustive native-code SBOM.

## Apache-licensed runtime components

Full text: [Apache-2.0](THIRD_PARTY_LICENSES/Apache-2.0.txt).

Kotlin and Kotlin libraries are maintained by JetBrains and contributors. AndroidX,
Compose, Material Icons, and Room are Android Open Source Project components.
Guava is maintained by Google and contributors. Retain their applicable copyright,
license, and NOTICE materials when distributing builds.

Apache Commons Compress, Codec, IO, and Lang are Apache Software Foundation projects.
Their artifact-specific LICENSE and NOTICE files are retained in
[THIRD_PARTY_LICENSES](THIRD_PARTY_LICENSES/README.md).

The runtime is **LiteRT**, `com.google.ai.edge.litert:litert:1.4.2` and `litert-api:1.4.2`,
not the formerly documented `org.tensorflow:tensorflow-lite:2.16.1`. The `org.tensorflow.lite`
Java package remains in use. Both AAR license files, including their Caffe attribution
and BSD conditions, are retained unmodified.

## Native archive library

Source at the bundled version:
<https://github.com/omicronapps/7-Zip-JBinding-4Android/tree/Release-16.02-2.03>.
Original 7-Zip-JBinding was implemented by Boris Brodski; 7-Zip is by Igor Pavlov.

Retained materials:

- [LGPL-2.1 text](THIRD_PARTY_LICENSES/LGPL-2.1.txt).
- [7-Zip notice](THIRD_PARTY_LICENSES/SevenZip-NOTICE.txt) and
  [p7zip notice](THIRD_PARTY_LICENSES/p7zip-NOTICE.txt).
- [JBinding license notice](THIRD_PARTY_LICENSES/SevenZip-JBinding-License.txt) and
  [authors](THIRD_PARTY_LICENSES/SevenZip-JBinding-AUTHORS.txt).
- [unRAR license restriction](THIRD_PARTY_LICENSES/unRAR-License.txt).
- [LZHAM MIT license](THIRD_PARTY_LICENSES/LZHAM-License.txt), retained from the native source tree.

The unRAR code may not be used to recreate the proprietary RAR compression algorithm.
Chika-eInk uses this dependency to read archives.

The APK includes native shared libraries. **This fact alone does not prove LGPL §6
compliance.** A distributor must establish the applicable source-supply and user
replacement/relinking route, including relevant build materials and allowance for
debugging modifications. This audit records the upstream source location but does
not certify that the current APK satisfies those requirements.

## Libron fonts

Libron v0.25 is by Nico Verbruggen, derived from Readerly and Newsreader.
Unmodified desktop TTFs from [the official v0.25 release](https://github.com/nicoverbruggen/libron/releases/tag/v0.25)
(`Libron.zip`) are bundled in `app/src/main/res/font/`.

[OFL-1.1 license and copyright notices](THIRD_PARTY_LICENSES/OFL-1.1-Libron.txt) and
[upstream COPYRIGHT](THIRD_PARTY_LICENSES/Libron-COPYRIGHT.txt) are retained and packaged
with the app. Font files are not relicensed under MPL. Anton and Archivo are no longer
bundled; their original license texts remain for historical attribution.

## Panel detector and training data

Author: Leandro Narosky (`leoxs22`).
[Model card and licensing correction](https://huggingface.co/leoxs22/manga-panel-detector-yolo26n#license).
The author corrected an erroneous Apache-2.0 label to **AGPL-3.0** in September 2026,
citing the Ultralytics YOLO26 base weights. The bundled TFLite bytes match the upstream
file; revision and SHA-256 are recorded in [docs/LICENSING.md](docs/LICENSING.md).
[AGPL-3.0 text](THIRD_PARTY_LICENSES/AGPL-3.0.txt) is retained.

The model was trained on **Manga109-s**, maintained by the Manga109 project at the
University of Tokyo. This notice discloses that training-data usage. The
[dataset owner's published terms](https://huggingface.co/datasets/hal-utokyo/Manga109-s)
permit commercial use of experimental results subject to conditions, require clear
dataset attribution for published models, and forbid dataset redistribution.
Dataset permissions do not replace the model's AGPL obligations. Dataset images are
not bundled in the app.

The model author describes copyleft requirements for projects distributing the model.
A combined-work licensing and corresponding-source decision remains open. We have not
relicensed the application, removed/replaced the weights, or changed detection behavior.
Do not describe the current model-bearing APK as Apache-only or MPL-only.

## Branding and artwork

Upstream notices reserve rights in the Chika / Chitra Katha logo and wordmark outside
its code license. The fork identifies its upstream origin without claiming ownership
or endorsement. Original promotional images and graphical branding were removed or
replaced; [docs/ARTWORK.md](docs/ARTWORK.md) records the current assets and retained history.

## Binary notices and source availability

Builds include this file, `LICENSE`, the audit/provenance documents, and
`THIRD_PARTY_LICENSES/` under **`assets/legal/`**. The app's menu also links to this
repository's notices. Corresponding application source is available at
<https://github.com/sztkp/chika-eink>; distributors should identify the exact commit
used for their binary. Native and model source obligations remain subject to the
release review above. No store or repository acceptance is claimed.
