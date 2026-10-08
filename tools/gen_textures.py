"""Generates the mod's pixel-art textures. Run: python3 tools/gen_textures.py (needs Pillow)."""
import random
from pathlib import Path
from PIL import Image

ROOT = Path(__file__).resolve().parent.parent / "src/main/resources/assets/athanor"
BLOCK = ROOT / "textures/block"
ITEM = ROOT / "textures/item"

ASPECTS = {
    "terra": (112, 84, 48), "aqua": (52, 120, 220), "ignis": (232, 84, 32), "aer": (230, 230, 150),
    "ordo": (236, 236, 252), "perditio": (72, 64, 72), "vita": (64, 200, 72), "metallum": (168, 168, 196),
}


def shade(c, f):
    return tuple(max(0, min(255, int(v * f))) for v in c[:3]) + ((c[3],) if len(c) == 4 else (255,))


def new(size=16, fill=(0, 0, 0, 0)):
    return Image.new("RGBA", (size, size), fill)


def bricks(rng):
    img = new()
    base = (74, 58, 92)
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), shade(base, rng.uniform(0.85, 1.12)))
    mortar = (176, 140, 64)
    for y in (3, 7, 11, 15):
        for x in range(16):
            img.putpixel((x, y), shade(mortar, rng.uniform(0.8, 1.0)))
    for row, y0 in enumerate((0, 4, 8, 12)):
        off = 0 if row % 2 == 0 else 4
        for x in (off, off + 8):
            for y in range(y0, y0 + 3):
                img.putpixel((x % 16, y), shade(mortar, 0.75))
    return img


def glass():
    img = new()
    frame = (176, 140, 64, 255)
    for i in range(16):
        for p in ((i, 0), (i, 15), (0, i), (15, i)):
            img.putpixel(p, frame)
    for p in ((1, 1), (14, 1), (1, 14), (14, 14), (2, 2), (13, 2), (2, 13), (13, 13)):
        img.putpixel(p, (220, 190, 110, 255))
    for i in range(4, 8):
        img.putpixel((i, 11 - i), (230, 220, 255, 140))
    return img


def resolver(rng, face):
    img = new()
    iron = (150, 150, 158)
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), shade(iron, rng.uniform(0.85, 1.05)))
    if face == "top":
        for y in range(3, 13):
            for x in range(3, 13):
                d = (x - 7.5) ** 2 + (y - 7.5) ** 2
                if d < 26:
                    img.putpixel((x, y), shade((110, 60, 160), 0.7 + 0.3 * (1 - d / 26)))
        for p in ((7, 7), (8, 8), (6, 9)):
            img.putpixel(p, (220, 180, 255, 255))
    elif face == "side":
        for x in range(16):
            img.putpixel((x, 0), shade(iron, 0.6))
            img.putpixel((x, 15), shade(iron, 0.6))
        for i, c in enumerate(ASPECTS.values()):
            x = 2 + (i % 4) * 3
            y = 5 if i < 4 else 9
            img.putpixel((x, y), c + (255,))
            img.putpixel((x + 1, y), shade(c, 0.8))
            img.putpixel((x, y + 1), shade(c, 0.8))
            img.putpixel((x + 1, y + 1), shade(c, 0.6))
    else:
        for y in range(16):
            for x in range(16):
                img.putpixel((x, y), shade((90, 90, 96), rng.uniform(0.85, 1.05)))
    return img


def core_front(rng, lit):
    img = bricks(rng)
    for y in range(4, 13):
        for x in range(4, 12):
            img.putpixel((x, y), (30, 22, 40, 255))
    if lit:
        for y in range(7, 12):
            for x in range(5, 11):
                t = (y - 7) / 5
                img.putpixel((x, y), (255, int(200 - 120 * t), int(60 + 40 * rng.random()), 255))
    for x in range(3, 13):
        img.putpixel((x, 3), (200, 165, 80, 255))
        img.putpixel((x, 13), (200, 165, 80, 255))
    return img


def crystal(color):
    img = new()
    shape = ["......#.........", ".....###........", "....#####.......", "...#######......",
             "...########.....", "..##########....", "..###########...", "...###########..",
             "....##########..", ".....#########..", "......#######...", ".......#####....",
             "........###.....", ".........#......", "................", "................"]
    for y, row in enumerate(shape):
        for x, ch in enumerate(row):
            if ch == "#":
                light = 1.25 - (x + y) / 28
                img.putpixel((x, y), shade(color, light))
    for p in ((6, 2), (5, 3), (6, 3), (5, 4)):
        img.putpixel(p, (255, 255, 255, 230))
    return img


def magnet(main, tip):
    img = new()
    pix = []
    for y in range(3, 14):
        for x in (3, 4, 5, 10, 11, 12):
            pix.append((x, y))
    for y in (13, 14):
        for x in range(3, 13):
            pix.append((x, y))
    for x, y in pix:
        img.putpixel((x, y), main + (255,))
    for y in (3, 4, 5):
        for x in (3, 4, 5, 10, 11, 12):
            img.putpixel((x, y), tip + (255,))
    for y in range(6, 14):
        img.putpixel((3, y), shade(main, 1.25))
        img.putpixel((10, y), shade(main, 1.25))
    return img


def icon(rng):
    small = core_front(rng, True)
    return small.resize((128, 128), Image.NEAREST)


def main():
    rng = random.Random(42)
    BLOCK.mkdir(parents=True, exist_ok=True)
    ITEM.mkdir(parents=True, exist_ok=True)
    bricks(rng).save(BLOCK / "athanor_bricks.png")
    glass().save(BLOCK / "alchemical_glass.png")
    for face in ("top", "side", "bottom"):
        resolver(rng, face).save(BLOCK / f"resolver_{face}.png")
    core_front(rng, False).save(BLOCK / "athanor_core_front.png")
    core_front(rng, True).save(BLOCK / "athanor_core_front_lit.png")
    for name, color in ASPECTS.items():
        crystal(color).save(ITEM / f"{name}_crystal.png")
    magnet((190, 40, 40), (210, 210, 220)).save(ITEM / "ore_magnet.png")
    magnet((60, 90, 200), (230, 200, 90)).save(ITEM / "item_magnet.png")
    icon(rng).save(ROOT / "icon.png")


if __name__ == "__main__":
    main()
