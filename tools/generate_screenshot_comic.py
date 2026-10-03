"""Package the original Roppongi demo manga for screenshots. MPL-2.0."""
import argparse
from pathlib import Path
from zipfile import ZipFile, ZIP_DEFLATED

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument("output", type=Path, help="Directory for the generated CBZ")
output = parser.parse_args().output
output.mkdir(parents=True, exist_ok=True)
artwork = Path(__file__).resolve().parents[1] / "docs/demo/roppongi.png"
archive_path = output / "六本木深夜決戦.cbz"
with ZipFile(archive_path, "w", ZIP_DEFLATED) as archive:
    archive.write(artwork, "001.png")
print(archive_path)
