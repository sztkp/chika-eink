# Experimental panel-detector training tools

These inherited scripts are retained for research reference. They are not part
of the Android build and have not been used to retrain Chika-eInk's bundled model.
No datasets or trained checkpoints are included. Historical run logs have been
removed; previous Git commits retain them.

## Scripts

- `scripts/comics_to_yolo.py`: converts an extracted COMICS panel annotation set
  containing `Images/` and `Annotations/` into YOLO labels (panel class 0).
- `scripts/manga109_to_yolo.py`: converts Manga109 page images and XML annotations
  into panel class 0 and text class 1 labels.
- `scripts/train.py`: experimental Ultralytics training, currently configured for
  Apple Silicon's MPS backend.
- `scripts/export_tflite.py`: experimental TFLite export with an embedded-NMS path
  and a SavedModel conversion fallback.

The Python environment is separate from Gradle. Conversion uses Pillow; training
uses Ultralytics; export also depends on the selected conversion backend. These
scripts do not supply a pinned, verified training environment.

## Example workflow

Obtain datasets under their applicable terms and use your own local paths:

```bash
cd training
python scripts/comics_to_yolo.py --src /path/to/comics-panels --out dataset
python scripts/manga109_to_yolo.py --root /path/to/manga109 --out dataset
python scripts/train.py --dataset dataset --model yolo11n.pt --epochs 100 --batch 16
python scripts/export_tflite.py --weights runs/detect/chika_panels/weights/best.pt \
  --out candidate.tflite
```

The scripts target panel/text classes and a decoder-supported tensor layout, but
export compatibility and detection quality must be checked on the actual output.
Do not automatically replace `app/src/main/assets/manga_panel_detector_int8.tflite`.
Validate input/output types and shapes, representative pages, LTR/RTL ordering,
and failure handling before considering a model change.

Datasets, pretrained weights, and training tools have separate terms. Retraining
does not automatically make an export permissively licensed. See the
[licensing audit](../docs/LICENSING.md) and retained notices.
