"""Generates block states, models, item definitions, loot tables, recipes and tags.
Run: python3 tools/gen_assets.py"""
import json
from pathlib import Path

RES = Path(__file__).resolve().parent.parent / "src/main/resources"
A = RES / "assets/athanor"
D = RES / "data/athanor"
NS = "athanor"
ASPECTS = ["terra", "aqua", "ignis", "aer", "ordo", "perditio", "vita", "metallum"]
# compound aspect -> its two basic aspects
COMPOUNDS = {"lux": ("ignis", "aer"), "potentia": ("ignis", "ordo"), "motus": ("aer", "ordo"),
             "praecantatio": ("ordo", "perditio"), "anima": ("vita", "aer"), "instrumentum": ("metallum", "ordo")}
FLAT_ITEMS = ["arcanium_ingot", "philosophers_stone", "growth_dust", "charm_swiftness", "charm_vitality",
              "charm_night_vision", "charm_tides", "charm_embers"]


def write(path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def block_item(name, model=None):
    write(A / f"items/{name}.json", {"model": {"type": "minecraft:model", "model": model or f"{NS}:block/{name}"}})


def flat_item(name, parent="minecraft:item/generated"):
    write(A / f"models/item/{name}.json", {"parent": parent, "textures": {"layer0": f"{NS}:item/{name}"}})
    write(A / f"items/{name}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})


def self_drop(name):
    write(D / f"loot_table/blocks/{name}.json", {
        "type": "minecraft:block",
        "pools": [{"rolls": 1, "entries": [{"type": "minecraft:item", "name": f"{NS}:{name}"}],
                   "conditions": [{"condition": "minecraft:survives_explosion"}]}],
        "random_sequence": f"{NS}:blocks/{name}"})


def main():
    # Simple cubes
    for name in ("athanor_bricks", "alchemical_glass"):
        write(A / f"blockstates/{name}.json", {"variants": {"": {"model": f"{NS}:block/{name}"}}})
        write(A / f"models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{name}"}})
        block_item(name)
        self_drop(name)

    # Resolver
    write(A / "blockstates/resolver.json", {"variants": {"": {"model": f"{NS}:block/resolver"}}})
    write(A / "models/block/resolver.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {
        "top": f"{NS}:block/resolver_top", "side": f"{NS}:block/resolver_side", "bottom": f"{NS}:block/resolver_bottom"}})
    block_item("resolver")
    self_drop("resolver")

    # Athanor core: facing + formed
    for lit in (False, True):
        suffix = "_formed" if lit else ""
        write(A / f"models/block/athanor_core{suffix}.json", {"parent": "minecraft:block/orientable", "textures": {
            "front": f"{NS}:block/athanor_core_front{'_lit' if lit else ''}",
            "side": f"{NS}:block/athanor_bricks", "top": f"{NS}:block/athanor_bricks"}})
    rot = {"north": 0, "east": 90, "south": 180, "west": 270}
    variants = {}
    for facing, y in rot.items():
        for formed in ("false", "true"):
            v = {"model": f"{NS}:block/athanor_core{'_formed' if formed == 'true' else ''}"}
            if y:
                v["y"] = y
            variants[f"facing={facing},formed={formed}"] = v
    write(A / "blockstates/athanor_core.json", {"variants": variants})
    block_item("athanor_core")
    self_drop("athanor_core")

    for a in ASPECTS:
        flat_item(f"{a}_crystal")
    flat_item("ore_magnet")
    flat_item("item_magnet")
    for c in COMPOUNDS:
        flat_item(f"{c}_crystal")
    for name in FLAT_ITEMS:
        flat_item(name)
    flat_item("alchemist_wand", "minecraft:item/handheld")

    # Ritual altar and pedestal
    write(A / "blockstates/ritual_altar.json", {"variants": {"": {"model": f"{NS}:block/ritual_altar"}}})
    write(A / "models/block/ritual_altar.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {
        "top": f"{NS}:block/ritual_altar_top", "side": f"{NS}:block/ritual_altar_side", "bottom": f"{NS}:block/athanor_bricks"}})
    block_item("ritual_altar")
    self_drop("ritual_altar")

    def box(f, t):
        faces = {d: {"texture": "#top" if d in ("up", "down") else "#side"} for d in ("north", "south", "east", "west", "up", "down")}
        return {"from": f, "to": t, "faces": faces}
    write(A / "blockstates/arcane_pedestal.json", {"variants": {"": {"model": f"{NS}:block/arcane_pedestal"}}})
    write(A / "models/block/arcane_pedestal.json", {
        "parent": "minecraft:block/block",
        "textures": {"particle": f"{NS}:block/athanor_bricks", "side": f"{NS}:block/athanor_bricks", "top": f"{NS}:block/pedestal_top"},
        "elements": [box([2, 0, 2], [14, 3, 14]), box([4, 3, 4], [12, 11, 12]), box([2, 11, 2], [14, 14, 14])]})
    block_item("arcane_pedestal")
    self_drop("arcane_pedestal")

    # Tags
    blocks = [f"{NS}:{n}" for n in ("athanor_bricks", "alchemical_glass", "resolver", "athanor_core", "ritual_altar", "arcane_pedestal")]
    write(RES / "data/minecraft/tags/block/mineable/pickaxe.json", {"values": blocks})
    write(D / "tags/item/aspect_crystals.json", {"values": [f"{NS}:{a}_crystal" for a in ASPECTS + list(COMPOUNDS)]})

    # Crafting recipes
    def shaped(name, pattern, key, result, count=1):
        write(D / f"recipe/{name}.json", {"type": "minecraft:crafting_shaped", "category": "building",
                                          "pattern": pattern, "key": key,
                                          "result": {"id": f"{NS}:{result}", "count": count}})
    shaped("athanor_bricks", ["SSS", "SCS", "SSS"], {"S": "minecraft:stone_bricks", "C": "minecraft:copper_ingot"}, "athanor_bricks", 8)
    shaped("alchemical_glass", [" G ", "GRG", " G "], {"G": "minecraft:glass", "R": "minecraft:redstone"}, "alchemical_glass", 4)
    shaped("resolver", ["III", "ICI", "SSS"], {"I": "minecraft:iron_ingot", "C": "minecraft:cauldron", "S": "minecraft:smooth_stone"}, "resolver")
    shaped("athanor_core", ["BGB", "BFB", "BBB"], {"B": f"{NS}:athanor_bricks", "G": "minecraft:gold_ingot", "F": "minecraft:blast_furnace"}, "athanor_core")

    shaped("alchemist_wand", ["  A", " G ", "S  "], {"A": "minecraft:amethyst_shard", "G": "minecraft:gold_ingot", "S": "minecraft:stick"}, "alchemist_wand")
    shaped("arcane_pedestal", [" G ", " B ", "BBB"], {"G": "minecraft:gold_ingot", "B": f"{NS}:athanor_bricks"}, "arcane_pedestal", 2)
    shaped("ritual_altar", ["AGA", "BDB", "BBB"], {"A": "minecraft:amethyst_shard", "G": "minecraft:gold_ingot",
                                                   "D": "minecraft:diamond", "B": f"{NS}:athanor_bricks"}, "ritual_altar")
    for c, (a, b) in COMPOUNDS.items():
        write(D / f"recipe/{c}_crystal.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                                                "ingredients": [f"{NS}:{a}_crystal", f"{NS}:{b}_crystal"],
                                                "result": {"id": f"{NS}:{c}_crystal", "count": 1}})


if __name__ == "__main__":
    main()
