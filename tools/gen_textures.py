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


COMPOUNDS = {
    "lux": (255, 242, 122), "potentia": (240, 160, 64), "motus": (176, 224, 240),
    "praecantatio": (176, 80, 240), "anima": (224, 160, 224), "instrumentum": (96, 128, 192),
}
CHARMS = {
    "charm_swiftness": (120, 200, 255), "charm_vitality": (240, 80, 110), "charm_night_vision": (60, 60, 200),
    "charm_tides": (40, 170, 170), "charm_embers": (250, 120, 30),
}


def compound_crystal(color):
    img = crystal(color)
    # A gold ring marks compound aspects.
    for p in ((2, 7), (3, 6), (12, 7), (11, 8), (7, 13), (8, 12)):
        img.putpixel(p, (230, 190, 80, 255))
    return img


def ingot(color):
    img = new()
    for y in range(6, 12):
        for x in range(2 + (11 - y) // 2, 14 - (y - 6) // 3):
            img.putpixel((x, y), shade(color, 1.2 - (y - 6) * 0.08))
    for x in range(5, 12):
        img.putpixel((x, 6), (255, 255, 255, 220))
    return img


def gem(color):
    img = new()
    for y in range(3, 13):
        w = 5 - abs(y - 7) // 1 if y <= 7 else 5 - (y - 7)
        for x in range(8 - max(w, 0), 8 + max(w, 0)):
            img.putpixel((x, y), shade(color, 1.25 - (x + y) / 30))
    for p in ((6, 5), (7, 4), (6, 6)):
        img.putpixel(p, (255, 230, 230, 255))
    return img


def dust(rng, color):
    img = new()
    for _ in range(26):
        x, y = rng.randrange(3, 13), rng.randrange(5, 14)
        img.putpixel((x, y), shade(color, rng.uniform(0.8, 1.3)))
    for p in ((5, 3), (10, 4), (12, 9)):
        img.putpixel(p, (255, 255, 200, 255))
    return img


def charm(color):
    img = new()
    gold = (220, 180, 70)
    for i in range(3, 13):
        img.putpixel((i, 2), gold + (255,))
    for y in range(2, 6):
        img.putpixel((3, y), gold + (255,))
        img.putpixel((12, y), gold + (255,))
    for y in range(6, 14):
        for x in range(4, 12):
            d = (x - 7.5) ** 2 + (y - 9.5) ** 2
            if d < 14:
                img.putpixel((x, y), gold + (255,))
            if d < 8:
                img.putpixel((x, y), shade(color, 1.2 - d / 16))
    img.putpixel((6, 8), (255, 255, 255, 255))
    return img


def wand():
    img = new()
    for i in range(12):
        img.putpixel((2 + i, 13 - i), (110, 70, 40, 255))
        if i < 11:
            img.putpixel((3 + i, 13 - i), (80, 50, 30, 255))
    for p in ((8, 7), (9, 6), (7, 8)):
        img.putpixel(p, (220, 180, 70, 255))
    for p in ((13, 1), (14, 1), (13, 2), (14, 2), (12, 2), (13, 0), (15, 2), (13, 3)):
        img.putpixel(p, (190, 120, 255, 255))
    return img


def altar(rng, face):
    img = bricks(rng)
    if face == "top":
        for y in range(16):
            for x in range(16):
                d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
                if 5.5 < d < 7.0:
                    img.putpixel((x, y), (220, 180, 70, 255))
                elif d < 2.5:
                    img.putpixel((x, y), (190, 120, 255, 255))
        for p in ((7, 3), (8, 12), (3, 8), (12, 7)):
            img.putpixel(p, (220, 180, 70, 255))
    else:
        for x in range(16):
            img.putpixel((x, 1), (220, 180, 70, 255))
            img.putpixel((x, 14), (220, 180, 70, 255))
        for p in ((7, 6), (8, 7), (7, 8), (8, 9), (7, 10)):
            img.putpixel(p, (190, 120, 255, 255))
    return img


def pedestal_top():
    img = new(fill=(74, 58, 92, 255))
    for i in range(16):
        for p in ((i, 0), (i, 15), (0, i), (15, i)):
            img.putpixel(p, (176, 140, 64, 255))
    for y in range(5, 11):
        for x in range(5, 11):
            img.putpixel((x, y), (120, 80, 170, 255))
    return img


def icon(rng):
    small = core_front(rng, True)
    return small.resize((128, 128), Image.NEAREST)


# ---- Magnum Opus expansion ----

def stone_base(rng, deep=False):
    img = new()
    base = (70, 70, 78) if deep else (125, 125, 125)
    for y in range(16):
        for x in range(16):
            f = rng.uniform(0.82, 1.12)
            if deep and y % 4 == 0:
                f *= 0.85
            img.putpixel((x, y), shade(base, f))
    return img


def ore(rng, color, deep=False, clusters=5):
    img = stone_base(rng, deep)
    for _ in range(clusters):
        cx, cy = rng.randrange(2, 14), rng.randrange(2, 14)
        for dx, dy in ((0, 0), (1, 0), (0, 1), (1, 1), (-1, 0)):
            if rng.random() < 0.85:
                x, y = max(0, min(15, cx + dx)), max(0, min(15, cy + dy))
                img.putpixel((x, y), shade(color, rng.uniform(0.75, 1.25)))
    return img


def rock_salt(rng):
    img = new()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), shade((232, 196, 196), rng.uniform(0.85, 1.08)))
    for _ in range(10):
        x, y = rng.randrange(16), rng.randrange(16)
        img.putpixel((x, y), (255, 245, 245, 255))
    for i in range(16):
        img.putpixel((i, (i * 7) % 16), shade((200, 150, 150), 0.95))
    return img


def metal_block(rng, color):
    img = new()
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), shade(color, rng.uniform(0.92, 1.05)))
    for i in range(16):
        img.putpixel((i, 0), shade(color, 1.3))
        img.putpixel((0, i), shade(color, 1.3))
        img.putpixel((i, 15), shade(color, 0.7))
        img.putpixel((15, i), shade(color, 0.7))
    return img


def alembic(rng, face):
    img = new()
    copper = (190, 110, 70)
    if face == "side":
        for y in range(16):
            for x in range(16):
                img.putpixel((x, y), shade((60, 50, 50), rng.uniform(0.9, 1.1)))
        for y in range(2, 14):
            for x in range(4, 12):
                d = ((x - 7.5) / 4) ** 2 + ((y - 9) / 5) ** 2
                if d < 1:
                    img.putpixel((x, y), shade((150, 210, 230), 0.8 + 0.3 * (1 - d)))
        for y in range(1, 5):
            for x in (7, 8):
                img.putpixel((x, y), shade(copper, 1.1))
        for x in range(8, 15):
            img.putpixel((x, 2 + (x - 8) // 3), shade(copper, 1.0))
        for x in range(16):
            img.putpixel((x, 15), shade(copper, 0.8))
    elif face == "top":
        for y in range(16):
            for x in range(16):
                img.putpixel((x, y), shade(copper, rng.uniform(0.8, 1.05)))
        for y in range(6, 10):
            for x in range(6, 10):
                img.putpixel((x, y), (30, 30, 34, 255))
    else:
        for y in range(16):
            for x in range(16):
                img.putpixel((x, y), shade(copper, rng.uniform(0.6, 0.8)))
    return img


def vessel(rng, face):
    img = new()
    glass = (200, 220, 230)
    lead = (88, 90, 110)
    for y in range(16):
        for x in range(16):
            img.putpixel((x, y), shade(lead, rng.uniform(0.85, 1.1)))
    if face == "side":
        for y in range(3, 14):
            for x in range(3, 13):
                d = ((x - 7.5) / 5) ** 2 + ((y - 8.5) / 5.5) ** 2
                if d < 1:
                    img.putpixel((x, y), shade(glass, 0.75 + 0.35 * (1 - d)) [:3] + (210,))
        for p in ((6, 5), (6, 6), (5, 7)):
            img.putpixel(p, (255, 255, 255, 255))
    else:
        for y in range(5, 11):
            for x in range(5, 11):
                img.putpixel((x, y), (220, 180, 70, 255))
    return img


def salt_lamp(rng):
    img = new()
    for y in range(16):
        for x in range(16):
            t = abs(x - 7.5) + abs(y - 7.5)
            img.putpixel((x, y), shade((250, 150, 110), 1.15 - t / 30 + rng.uniform(-0.06, 0.06)))
    for _ in range(8):
        img.putpixel((rng.randrange(16), rng.randrange(16)), (255, 220, 190, 255))
    return img


def pile(rng, color, sparkle=(255, 255, 255)):
    img = new()
    for y in range(7, 14):
        half = (y - 6) + 1
        for x in range(8 - half, 8 + half):
            if 0 <= x < 16:
                img.putpixel((x, y), shade(color, rng.uniform(0.8, 1.15)))
    for p in ((7, 8), (9, 10), (5, 12)):
        img.putpixel(p, sparkle + (255,))
    return img


def lump(rng, color):
    img = new()
    for y in range(4, 13):
        for x in range(3, 13):
            d = ((x - 7.5) / 5) ** 2 + ((y - 8) / 4.5) ** 2
            if d < 1 - rng.uniform(0, 0.15):
                img.putpixel((x, y), shade(color, 1.2 - d * 0.5 + rng.uniform(-0.08, 0.08)))
    return img


def droplet(color):
    img = new()
    for y in range(2, 14):
        w = (y - 2) * 0.5 if y < 9 else 4 - (y - 9) * 0.8
        for x in range(int(8 - w), int(8 + w) + 1):
            if 0 <= x < 16:
                img.putpixel((x, y), shade(color, 1.25 - (x + y) / 32))
    img.putpixel((6, 9), (255, 255, 255, 255))
    img.putpixel((6, 10), (255, 255, 255, 200))
    return img


def orb(color, glow=(255, 255, 255)):
    img = new()
    for y in range(16):
        for x in range(16):
            d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if d < 6:
                img.putpixel((x, y), shade(color, 1.3 - d / 8))
    for p in ((5, 5), (6, 5), (5, 6)):
        img.putpixel(p, glow + (255,))
    return img


def bottle(color, cork=(140, 100, 60)):
    img = new()
    glass = (220, 235, 245, 200)
    for y in range(6, 15):
        for x in range(4, 12):
            d = ((x - 7.5) / 4) ** 2 + ((y - 10.5) / 4.5) ** 2
            if d < 1:
                img.putpixel((x, y), glass if y < 8 else shade(color, 1.2 - d * 0.4))
    for y in range(3, 6):
        for x in (7, 8):
            img.putpixel((x, y), glass)
    for x in (6, 7, 8, 9):
        img.putpixel((x, 2), cork + (255,))
    img.putpixel((6, 10), (255, 255, 255, 230))
    return img


def lens():
    img = new()
    gold = (220, 180, 70, 255)
    for y in range(16):
        for x in range(16):
            d = ((x - 6.5) ** 2 + (y - 6.5) ** 2) ** 0.5
            if 4.2 < d < 5.6:
                img.putpixel((x, y), gold)
            elif d <= 4.2:
                img.putpixel((x, y), (170, 220, 255, 150))
    for i in range(4):
        img.putpixel((10 + i, 10 + i), (110, 70, 40, 255))
        img.putpixel((11 + i, 10 + i), (80, 50, 30, 255))
    img.putpixel((5, 4), (255, 255, 255, 255))
    return img


def mirror():
    img = new()
    silver = (200, 205, 215, 255)
    for y in range(16):
        for x in range(16):
            d = ((x - 7.5) ** 2 + (y - 6.0) ** 2) ** 0.5
            if 4.6 < d < 6:
                img.putpixel((x, y), silver)
            elif d <= 4.6:
                img.putpixel((x, y), (150, 160, 190, 255) if (x + y) % 5 else (235, 240, 255, 255))
    for y in range(12, 16):
        img.putpixel((7, y), (110, 70, 40, 255))
        img.putpixel((8, y), (80, 50, 30, 255))
    return img


def ring():
    img = new()
    snake = (60, 150, 80)
    for y in range(16):
        for x in range(16):
            d = ((x - 7.5) ** 2 + (y - 7.5) ** 2) ** 0.5
            if 4 < d < 6.3:
                img.putpixel((x, y), shade(snake, 1.25 - abs(d - 5.1) / 2))
    for p in ((12, 5), (13, 5), (12, 4)):
        img.putpixel(p, (200, 40, 40, 255))
    img.putpixel((11, 4), (255, 230, 80, 255))
    return img


def tablet():
    img = new()
    green = (40, 170, 90)
    for y in range(1, 15):
        for x in range(3, 13):
            img.putpixel((x, y), shade(green, 1.15 - (x + y) / 40))
    for y in (4, 6, 8, 10, 12):
        for x in range(5, 11):
            if (x + y) % 3:
                img.putpixel((x, y), (200, 255, 210, 255))
    for x in range(3, 13):
        img.putpixel((x, 1), (220, 180, 70, 255))
        img.putpixel((x, 14), (220, 180, 70, 255))
    return img


TOOL_SHAPES = {
    "pickaxe": ["................", "...HHHHHHHH.....", "..H........H....", ".......S........",
                "......S.........", ".....S..........", "....S...........", "...S............",
                "..S.............", ".S..............", "S...............", "................",
                "................", "................", "................", "................"],
    "axe": ["................", "......HHH.......", ".....HHHHH......", ".....HHHS.......",
            "......HS........", ".....S..........", "....S...........", "...S............",
            "..S.............", ".S..............", "S...............", "................",
            "................", "................", "................", "................"],
    "shovel": ["................", "........HHH.....", ".......HHHHH....", ".......HHHH.....",
               "........HH......", ".......S........", "......S.........", ".....S..........",
               "....S...........", "...S............", "..S.............", ".S..............",
               "S...............", "................", "................", "................"],
    "sword": ["..............HH", ".............HHH", "............HHH.", "...........HHH..",
              "..........HHH...", ".........HHH....", "........HHH.....", "...G...HHH......",
              "....G.HHH.......", ".....GHH........", "......SG........", ".....S..G.......",
              "....S...........", "...S............", "..S.............", "................"],
    "hoe": ["................", "....HHHHH.......", "...H....S.......", "........S.......",
            ".......S........", "......S.........", ".....S..........", "....S...........",
            "...S............", "..S.............", ".S..............", "S...............",
            "................", "................", "................", "................"],
}


def tool(kind, head):
    img = new()
    for y, row in enumerate(TOOL_SHAPES[kind]):
        for x, ch in enumerate(row):
            if ch == "H":
                img.putpixel((x, y + 2 if kind != "sword" else y), shade(head, 1.25 - (x + y) / 40))
            elif ch == "S":
                img.putpixel((x, y + 2 if kind != "sword" else y), (110, 70, 40, 255))
            elif ch == "G":
                img.putpixel((x, y), (220, 180, 70, 255))
    return img


def effect_icon(color, symbol):
    img = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    for y in range(18):
        for x in range(18):
            d = ((x - 8.5) ** 2 + (y - 8.5) ** 2) ** 0.5
            if d < 8:
                img.putpixel((x, y), shade(color, 1.2 - d / 12))
    for x, y in symbol:
        img.putpixel((x, y), (255, 255, 255, 255))
    return img


def expansion(rng):
    ores = {"cinnabar": (200, 40, 40), "silver": (220, 225, 235), "lead": (90, 95, 130)}
    for name, color in ores.items():
        ore(rng, color).save(BLOCK / f"{name}_ore.png")
        ore(rng, color, deep=True).save(BLOCK / f"deepslate_{name}_ore.png")
    rock_salt(rng).save(BLOCK / "rock_salt.png")
    metal_block(rng, (200, 205, 220)).save(BLOCK / "silver_block.png")
    metal_block(rng, (85, 90, 120)).save(BLOCK / "lead_block.png")
    for face in ("top", "side", "bottom"):
        alembic(rng, face).save(BLOCK / f"alembic_{face}.png")
    vessel(rng, "side").save(BLOCK / "hermetic_vessel_side.png")
    vessel(rng, "top").save(BLOCK / "hermetic_vessel_top.png")
    salt_lamp(rng).save(BLOCK / "salt_lamp.png")

    crystal((200, 40, 40)).save(ITEM / "cinnabar.png")
    droplet((200, 205, 215)).save(ITEM / "quicksilver.png")
    pile(rng, (240, 236, 236)).save(ITEM / "salt.png")
    pile(rng, (230, 210, 60), (255, 255, 180)).save(ITEM / "sulfur.png")
    lump(rng, (210, 215, 225)).save(ITEM / "raw_silver.png")
    lump(rng, (90, 95, 125)).save(ITEM / "raw_lead.png")
    ingot((215, 220, 232)).save(ITEM / "silver_ingot.png")
    ingot((95, 100, 135)).save(ITEM / "lead_ingot.png")
    orb((70, 60, 80), (180, 160, 200)).save(ITEM / "prima_materia.png")
    orb((30, 28, 32), (120, 120, 120)).save(ITEM / "nigredo.png")
    orb((235, 235, 240)).save(ITEM / "albedo.png")
    orb((240, 200, 50)).save(ITEM / "citrinitas.png")
    orb((210, 30, 40), (255, 200, 120)).save(ITEM / "rubedo.png")
    bottle((150, 200, 255)).save(ITEM / "aqua_vitae.png")
    bottle((230, 40, 60), (220, 180, 70)).save(ITEM / "elixir_of_life.png")
    bottle((90, 220, 140)).save(ITEM / "panacea.png")
    bottle((150, 60, 220)).save(ITEM / "alkahest.png")
    lens().save(ITEM / "alchemist_lens.png")
    mirror().save(ITEM / "quicksilver_mirror.png")
    ring().save(ITEM / "ouroboros_ring.png")
    tablet().save(ITEM / "emerald_tablet.png")
    for kind in TOOL_SHAPES:
        tool(kind, (150, 110, 220)).save(ITEM / f"arcanium_{kind}.png")
    effects = ROOT / "textures/mob_effect"
    effects.mkdir(parents=True, exist_ok=True)
    effect_icon((210, 40, 60), [(8, y) for y in range(4, 14)] + [(x, 7) for x in range(5, 12)]).save(effects / "aeternitas.png")
    effect_icon((90, 210, 140), [(x, 8) for x in range(5, 13)] + [(8, y) for y in range(5, 13)]).save(effects / "purity.png")


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
    for name, color in COMPOUNDS.items():
        compound_crystal(color).save(ITEM / f"{name}_crystal.png")
    for name, color in CHARMS.items():
        charm(color).save(ITEM / f"{name}.png")
    ingot((150, 110, 220)).save(ITEM / "arcanium_ingot.png")
    gem((200, 30, 50)).save(ITEM / "philosophers_stone.png")
    dust(rng, (90, 220, 90)).save(ITEM / "growth_dust.png")
    wand().save(ITEM / "alchemist_wand.png")
    altar(rng, "top").save(BLOCK / "ritual_altar_top.png")
    altar(rng, "side").save(BLOCK / "ritual_altar_side.png")
    pedestal_top().save(BLOCK / "pedestal_top.png")
    icon(rng).save(ROOT / "icon.png")
    expansion(random.Random(7))


if __name__ == "__main__":
    main()
