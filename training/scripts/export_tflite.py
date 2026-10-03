#!/usr/bin/env python3
"""Export a trained .pt model to the .tflite the Kuro app loads.

Target: input [1,640,640,3] float32, output [1,N,6] end-to-end (x1,y1,x2,y2,score,cls) — produced
by exporting with embedded NMS. The Android asset location is app/src/main/assets; no
pipeline change since the shared YoloPanelDecoder already handles the [1,N,6] layout.

Primary path: Ultralytics `export(format='tflite', nms=True)`. If the onnx2tf→tflite step stalls
(it can on some architectures), fall back to converting the produced *_saved_model with TFLite's
own converter — the workaround that succeeded during the mosesb experiment.

Usage:
  python export_tflite.py --weights runs/detect/kuro/weights/best.pt [--out model.tflite]
"""
import argparse, os, glob, sys


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--weights", required=True)
    ap.add_argument("--out", default="manga_panel_detector_int8.tflite")
    ap.add_argument("--imgsz", type=int, default=640)
    a = ap.parse_args()

    from ultralytics import YOLO
    model = YOLO(a.weights)
    saved_model_dir = None
    try:
        result = model.export(format="tflite", imgsz=a.imgsz, nms=True)
        print("ultralytics export produced:", result)
        # ultralytics returns the tflite path (or a dir); find a .tflite
        cand = result if str(result).endswith(".tflite") else None
        if not cand:
            hits = glob.glob(os.path.join(os.path.dirname(a.weights), "*_saved_model", "*.tflite"))
            cand = hits[0] if hits else None
        if cand and os.path.exists(cand):
            os.replace(cand, a.out)
            print("wrote", a.out)
            return
    except Exception as e:
        print("primary export failed/stalled:", repr(e)[:200], file=sys.stderr)

    # Fallback: convert the saved_model the export left behind.
    hits = glob.glob(os.path.join(os.path.dirname(a.weights), "*_saved_model"))
    if not hits:
        print("no saved_model to fall back on; re-run export with nms first", file=sys.stderr)
        sys.exit(1)
    saved_model_dir = hits[0]
    import tensorflow as tf
    conv = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
    conv.optimizations = [tf.lite.Optimize.DEFAULT]
    open(a.out, "wb").write(conv.convert())
    print("wrote (fallback)", a.out)


if __name__ == "__main__":
    main()
