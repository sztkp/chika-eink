# Licensing review

The combined app is distributed under AGPL-3.0, with MPL-covered application source
additionally available under AGPL through MPL 2.0 section 3.3. The model's
corresponding-source review remains open; a published release is not evidence that
all distribution obligations are satisfied.

## Combined-work license

**Finding.** The distribution route is the AGPL Larger Work described in
[DISTRIBUTION_LICENSE.md](../DISTRIBUTION_LICENSE.md). The original
[MPL-2.0 text](../LICENSE) and recipients' MPL rights are preserved; third-party
components retain their own terms.

**Evidence.** [MPL 2.0 sections 1.12 and 3.3](https://www.mozilla.org/en-US/MPL/2.0/)
allow eligible MPL-covered code to be additionally distributed under a secondary
license when combined with a work under that license. AGPL-3.0 is a secondary
license. The detector's upstream declaration supplies the AGPL component.

**Review and consequence.** This route depends on the covered code being eligible
for secondary licensing and on satisfying the AGPL source obligations. Preserve
applicable notices and check inherited/new contributions for incompatible terms.
The root MPL license alone does not describe the combined APK's distribution terms.

## Bundled detector and source materials

**Finding.** The bundled detector is declared AGPL-3.0 upstream, not Apache-2.0.

**Evidence.** The [pinned model card](https://huggingface.co/leoxs22/manga-panel-detector-yolo26n/blob/40a2854663d537563cfb95c370288a84c6505b9a/README.md)
identifies Ultralytics YOLO26 base weights and explains that the author's previous
Apache grant was mistaken. That label cannot establish alternative permission.

| Material | Identity |
| --- | --- |
| Bundled file | `app/src/main/assets/manga_panel_detector_int8.tflite` |
| TFLite SHA-256 | `b1a7d8d4492e04a777ae0d3efd9dc1fbd6e8f361971eadb813279ce3dfd1b464` |
| Matching upstream revision | `40a2854663d537563cfb95c370288a84c6505b9a` |
| Editable FP32 checkpoint SHA-256 | `73e0fb587ea3afe0d17aa9f0c3b1f5a8001b3ecbc3c77091e0730654b0da9146` |
| Framework identified by checkpoint metadata | Ultralytics 8.4.31, commit [`65b736045f7e8d54bbf3fd27709f4b1321b3b532`](https://github.com/ultralytics/ultralytics/tree/65b736045f7e8d54bbf3fd27709f4b1321b3b532) |

The published [v0.3.0 release](https://github.com/sztkp/chika-eink/releases/tag/v0.3.0)
includes matching app source and a dependency source bundle containing the editable
checkpoint, pinned Ultralytics source, model card, and notices.
[Experimental training/export scripts](../training/README.md) are not a verified
reproduction of the bundled model.

**Unresolved issue.** Exact INT8 export reproduction and complete corresponding-source
coverage remain unverified. Collecting a checkpoint and framework source does not
establish that all required build/export materials are present.

**Distribution consequence.** Verify source completeness and the applicable AGPL
obligations before claiming clearance. If that cannot be established, obtain
sufficient alternative permission or use a model with a validated licensing route.
Including the AGPL text alone does not establish compliance.

## Training data

**Finding and evidence.** The model was trained on Manga109-s. The
[dataset owner's terms](https://huggingface.co/datasets/hal-utokyo/Manga109-s)
permit commercial use of experimental results, require clear dataset attribution
for published models, and prohibit dataset redistribution. They also restrict
publication or sale of dataset images.

**Distribution consequence.** Keep the training disclosure in
[THIRD_PARTY_NOTICES.md](../THIRD_PARTY_NOTICES.md); do not include dataset images
or annotations in releases. These permissions do not replace the model's AGPL
obligations. Any source-reproduction route requiring dataset access must respect
the dataset's separate terms.

## Runtime libraries and fonts

**Finding and evidence.** Archives use Apache Commons Compress for CBZ/ZIP only.
The source and Gradle catalog contain no 7-Zip-JBinding backend or dependency, so
the unRAR restriction is not part of the current archive implementation.
LiteRT (`com.google.ai.edge.litert:litert` and `litert-api`, 1.4.2) supplies inference;
its AAR license texts include Caffe's BSD attribution. The Commons Compress,
Codec, IO, and Lang artifact LICENSE/NOTICE files are retained.

Libron v0.25 is OFL-1.1, derived from Readerly and Newsreader. The four bundled TTFs
were checked against the official desktop release. Historical Anton/Archivo
license texts are retained, but those fonts are not bundled. Exact text sources
are recorded in [license provenance](../THIRD_PARTY_LICENSES/README.md).

**Review and consequence.** The [component inventory](../THIRD_PARTY_NOTICES.md)
is not an exhaustive native-code SBOM. Check resolved transitive dependencies and
native subcomponent terms when preparing a release; top-level dependency metadata
and included license texts alone do not establish all source/notice obligations.

## Recheck when components change

1. Resolve `debugRuntimeClasspath` and `releaseRuntimeClasspath` with
   `./gradlew :app:dependencies --configuration <configuration>`.
2. Inspect artifact POMs, embedded LICENSE/NOTICE files, and native subcomponents.
3. Update the component inventory and license provenance; verify their inclusion
   under `assets/legal/` in the APK.
4. Reassess combined-work compatibility and corresponding-source coverage.
