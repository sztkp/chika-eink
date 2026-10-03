"""Generate Chika-eInk's original monochrome book icons (MPL-2.0).

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


for name, size in {
    "app/src/main/res/mipmap-xxxhdpi/ic_launcher.png": 192,
    "app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png": 192,
    "fastlane/metadata/android/en-US/images/icon.png": 512,
    "iosApp/Sources/Assets.xcassets/AppIcon.appiconset/icon-1024.png": 1024,
    "iosApp/Sources/Assets.xcassets/ChikaMark.imageset/icon-maroon.png": 256,
}.items():
    target = Path(name)
    target.parent.mkdir(parents=True, exist_ok=True)
    book_icon(size).save(target)
