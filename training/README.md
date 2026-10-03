# Chika panel-detector training (inherited by Chika-eInk)

These are upstream experimental training notes, retained for reference. The data-ready
and machine-specific statements below describe upstream work, not this fork's checkout.
Chika-eInk has not changed or retrained the bundled detector.

Train a replacement for the bundled `manga_panel_detector_int8.tflite` on **diverse** data so it
works on color/Western comics *and* manga — fixing the root cause (the current model is trained
only on B&W Manga109) instead of patching its output. Runs locally on Apple Silicon (**MPS**).

Output model matches the app's decoder with **zero pipeline changes**: input `[1,640,640,3]`
float32, output `[1,N,6]` (`x1,y1,x2,y2,score,cls`), classes **panel=0, text=1**.

> ⚠️ **Disk:** this machine had ~21 GB free. The full COMICS page set (129 GB) is **not** needed —
> the panel-annotation zip already bundles its images. Manga109 (~20 GB) may need you to free space
> or extract it to an external drive and point `--root` at it.

> ⚠️ **License:** Manga109 is academic/research-licensed; a model trained on it carries the same
> distribution ambiguity already noted in `THIRD_PARTY_NOTICES.md`. COMICS images are public domain.

## What's already done
- **Western data ready:** `data/panels_annotations.zip` (COMICS, UMIACS) downloaded + converted to
  YOLO into `dataset/` (443 public-domain pages, panel boxes). See `scripts/comics_to_yolo.py`.
- All scripts written + the full chain sanity-checked on MPS.

## Step 1 — get the manga data (the gated part)
Manga109 panel boxes are the only real manga source and need a one-time form (no academic email
required — just describe the use case; approval ~2–3 days):
- Request **Manga109-s** (the redistributable subset) here: http://www.manga109.org/en/download_s.html
- Unzip the approved download somewhere with space; you want this layout:
  `<root>/annotations/<Title>.xml` and `<root>/images/<Title>/<index>.jpg`

## Step 2 — convert + merge into the YOLO dataset
```bash
cd training
# (COMICS already converted into dataset/. If starting fresh:)
python scripts/comics_to_yolo.py   --src data/peek/panels --out dataset
# Add manga once you have it:
python scripts/manga109_to_yolo.py --root /path/to/manga109 --out dataset
```
Both write into `dataset/images/{train,val}` + `dataset/labels/{train,val}` (panel=0, text=1).

## Step 3 — train on MPS
```bash
python scripts/train.py --dataset dataset --model yolo11n.pt --epochs 100 --batch 16
# best weights → runs/detect/chika_panels/weights/best.pt
```
Heavy color augmentation (HSV/mosaic/mixup) is on by default — it's what bridges B&W↔color.

## Step 4 — export the tflite
```bash
python scripts/export_tflite.py --weights runs/detect/chika_panels/weights/best.pt \
  --out manga_panel_detector_int8.tflite
```

## Step 5 — validate before replacing the Android model

The Android model is intentionally unchanged in this fork. Test any experimental
export's decoder compatibility and detection quality separately before replacing
`app/src/main/assets/manga_panel_detector_int8.tflite`. Rebuild with
`./gradlew :app:assembleDebug` and verify representative comics on an Android device.

## Honest expectations
- **Will improve a lot:** color/Western comics (Batman), clean manga grids.
- **Won't fully fix:** truly *borderless* dynamic manga spreads (Dandadan) — there's no panel edge
  to learn; those keep using the reliability whole-page fallback. That's a fundamental limit, not a
  data gap you can close with Manga109 (which is mostly traditional layouts).
