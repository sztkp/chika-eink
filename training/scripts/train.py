#!/usr/bin/env python3
"""Train the Chika panel detector on the merged 2-class YOLO dataset, on Apple Silicon (MPS).

Produces a model whose I/O matches the app's decoder exactly (input [1,640,640,3], 2 classes:
panel=0, text=1). Heavy COLOR augmentation is the point — it bridges B&W manga ↔ color comics,
which is the single most important lever per the detection research.

Usage:
  python train.py --dataset <dataset dir> [--model yolo11n.pt] [--epochs 100] [--batch 16]
The dataset dir must contain images/{train,val} and labels/{train,val}.
"""
import argparse, os
from ultralytics import YOLO


def write_data_yaml(dataset):
    path = os.path.join(dataset, "data.yaml")
    with open(path, "w") as f:
        f.write(
            f"path: {os.path.abspath(dataset)}\n"
            "train: images/train\n"
            "val: images/val\n"
            "names:\n  0: panel\n  1: text\n"
        )
    return path


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--dataset", required=True)
    ap.add_argument("--model", default="yolo11n.pt", help="base weights to transfer-learn from")
    ap.add_argument("--epochs", type=int, default=100)
    ap.add_argument("--batch", type=int, default=16)
    ap.add_argument("--imgsz", type=int, default=640)
    ap.add_argument("--name", default="chika_panels")
    # Small-dataset fine-tune knobs: a high lr0 + heavy aug degrades YOLO11n's
    # pretrained COCO features faster than 398 imgs can re-teach them (the v1 run
    # peaked at epoch 1 then declined). Lower lr, freeze the backbone, lighten aug.
    # v3: even the lr0=0.001 run (chika_panels_v2-2) peaked at epoch 2-3 during warmup
    # then collapsed as the 3-epoch warmup ramped lr past ~0.0015 — the model converges
    # almost instantly on this easy single-class 398-img set, so a hot lr only erodes it.
    # Flat low lr, ~1-epoch warmup, NO mosaic, short schedule, save every epoch so a
    # fitness-metric quirk can't discard the good (high-P-AND-R) checkpoint.
    ap.add_argument("--lr0", type=float, default=0.0005)
    ap.add_argument("--freeze", type=int, default=10, help="freeze first N modules (backbone)")
    ap.add_argument("--patience", type=int, default=15)
    a = ap.parse_args()

    data_yaml = write_data_yaml(a.dataset)
    model = YOLO(a.model)
    model.train(
        data=data_yaml, epochs=a.epochs, batch=a.batch, imgsz=a.imgsz, device="mps",
        name=a.name, patience=a.patience, cache=False, save_period=1,
        lr0=a.lr0, freeze=a.freeze, cos_lr=True, warmup_epochs=1.0, warmup_bias_lr=0.0,
        # Keep COLOR aug (bridge B&W↔color) — that's the point. Drop mosaic/mixup entirely;
        # composite aug hurt recall on this small single-class set.
        hsv_h=0.015, hsv_s=0.7, hsv_v=0.4, fliplr=0.5, flipud=0.0,
        degrees=2.0, translate=0.1, scale=0.3, shear=1.0, mosaic=0.0, mixup=0.0,
    )
    print("done. best weights: runs/detect/%s/weights/best.pt" % a.name)


if __name__ == "__main__":
    main()
