"""Generates block states, models, item definitions, loot tables, recipes and tags.
Run: python3 tools/gen_assets.py"""
import json
from pathlib import Path

RES = Path(__file__).resolve().parent.parent / "src/main/resources"
A = RES / "assets/athanor"
D = RES / "data/athanor"
NS = "athanor"
ASPECTS = ["terra", "aqua", "ignis", "aer", "ordo", "perditio", "vita", "metallum"]


def write(path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def block_item(name, model=None):
    write(A / f"items/{name}.json", {"model": {"type": "minecraft:model", "model": model or f"{NS}:block/{name}"}})


def flat_item(name):
    write(A / f"models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/{name}"}})
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

    # Tags
    blocks = [f"{NS}:{n}" for n in ("athanor_bricks", "alchemical_glass", "resolver", "athanor_core")]
    write(RES / "data/minecraft/tags/block/mineable/pickaxe.json", {"values": blocks})
    write(D / "tags/item/aspect_crystals.json", {"values": [f"{NS}:{a}_crystal" for a in ASPECTS]})

    # Crafting recipes
    def shaped(name, pattern, key, result, count=1):
        write(D / f"recipe/{name}.json", {"type": "minecraft:crafting_shaped", "category": "building",
                                          "pattern": pattern, "key": key,
                                          "result": {"id": f"{NS}:{result}", "count": count}})
    shaped("athanor_bricks", ["SSS", "SCS", "SSS"], {"S": "minecraft:stone_bricks", "C": "minecraft:copper_ingot"}, "athanor_bricks", 8)
    shaped("alchemical_glass", [" G ", "GRG", " G "], {"G": "minecraft:glass", "R": "minecraft:redstone"}, "alchemical_glass", 4)
    shaped("resolver", ["III", "ICI", "SSS"], {"I": "minecraft:iron_ingot", "C": "minecraft:cauldron", "S": "minecraft:smooth_stone"}, "resolver")
    shaped("athanor_core", ["BGB", "BFB", "BBB"], {"B": f"{NS}:athanor_bricks", "G": "minecraft:gold_ingot", "F": "minecraft:blast_furnace"}, "athanor_core")


if __name__ == "__main__":
    main()
