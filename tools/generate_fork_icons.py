"""Generate Kuro's original monochrome book icons (MPL-2.0).

Requires Pillow. No upstream artwork or external image inputs are used.
Run from the repository root: python3 tools/generate_fork_icons.py
"""
from pathlib import Path
from PIL import Image, ImageDraw

# Same simple open-book geometry as the Android vector, on a white ground.
LEFT = [(28, 32), (50, 36), (50, 78), (28, 74)]
RIGHT = [(58, 36), (80, 32), (80, 74), (58, 78)]


def book_icon(size):
    image = Image.new("RGB", (size, size), "white")
    draw = ImageDraw.Draw(image)
    for page in (LEFT, RIGHT):
        draw.polygon([(round(x * size / 108), round(y * size / 108)) for x, y in page], fill="black")
    return image


# All supported Android versions (API 26+) use the vector adaptive icons.
# Only the store listing needs a raster icon.
target = Path("fastlane/metadata/android/en-US/images/icon.png")
target.parent.mkdir(parents=True, exist_ok=True)
book_icon(512).save(target)
