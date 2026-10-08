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
                   "condition": {"type": "minecraft:survives_explosion"}}],
        "random_sequence": f"{NS}:blocks/{name}"})


ORES = {"cinnabar_ore": ("cinnabar", 1, 2), "deepslate_cinnabar_ore": ("cinnabar", 1, 2),
        "silver_ore": ("raw_silver", 1, 1), "deepslate_silver_ore": ("raw_silver", 1, 1),
        "lead_ore": ("raw_lead", 1, 1), "deepslate_lead_ore": ("raw_lead", 1, 1),
        "rock_salt": ("salt", 2, 4)}
EXPANSION_ITEMS = ["cinnabar", "quicksilver", "salt", "sulfur", "raw_silver", "raw_lead", "silver_ingot", "lead_ingot",
                   "prima_materia", "nigredo", "albedo", "citrinitas", "rubedo", "aqua_vitae", "elixir_of_life", "panacea",
                   "alkahest", "alchemist_lens", "quicksilver_mirror", "ouroboros_ring", "emerald_tablet"]
TOOLS = ["pickaxe", "axe", "shovel", "sword", "hoe"]


def cube(name, texture=None):
    write(A / f"blockstates/{name}.json", {"variants": {"": {"model": f"{NS}:block/{name}"}}})
    write(A / f"models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{texture or name}"}})
    block_item(name)


def ore_drop(name, item, lo, hi):
    child = {"type": "minecraft:item", "name": f"{NS}:{item}", "modifier": []}
    if (lo, hi) != (1, 1):
        child["modifier"].append({"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}})
    child["modifier"] += [{"type": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                          {"type": "minecraft:explosion_decay"}]
    write(D / f"loot_table/blocks/{name}.json", {
        "type": "minecraft:block",
        "pools": [{"rolls": 1, "entries": [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "condition": "minecraft:tool/can_silk_touch", "name": f"{NS}:{name}"}, child]}]}],
        "random_sequence": f"{NS}:blocks/{name}"})


def ore_feature(name, stone, deep, size, count, lo, hi, shape="trapezoid"):
    targets = [{"state": f"{NS}:{stone}", "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:stone_ore_replaceables"}}]
    if deep:
        targets.append({"state": f"{NS}:{deep}", "target": {"predicate_type": "minecraft:tag_match", "tag": "minecraft:deepslate_ore_replaceables"}})
    write(D / f"worldgen/feature/{name}.json", {"type": "minecraft:ore", "discard_chance_on_air_exposure": 0.0,
                                                "size": size, "targets": targets})
    write(D / f"worldgen/placed_feature/{name}.json", {"feature": f"{NS}:{name}", "placement": [
        {"type": "minecraft:count", "count": count}, {"type": "minecraft:in_square"},
        {"type": "minecraft:height_range", "height": {"type": f"minecraft:{shape}",
                                                       "min_inclusive": {"absolute": lo}, "max_inclusive": {"absolute": hi}}},
        {"type": "minecraft:biome"}]})


def expansion(shaped):
    for name, (item, lo, hi) in ORES.items():
        cube(name)
        ore_drop(name, item, lo, hi)
    for name in ("silver_block", "lead_block", "salt_lamp"):
        cube(name)
        self_drop(name)
    write(A / "blockstates/alembic.json", {"variants": {"": {"model": f"{NS}:block/alembic"}}})
    write(A / "models/block/alembic.json", {"parent": "minecraft:block/cube_bottom_top", "textures": {
        "top": f"{NS}:block/alembic_top", "side": f"{NS}:block/alembic_side", "bottom": f"{NS}:block/alembic_bottom"}})
    block_item("alembic")
    self_drop("alembic")

    def box(f, t):
        faces = {d: {"texture": "#top" if d in ("up", "down") else "#side"} for d in ("north", "south", "east", "west", "up", "down")}
        return {"from": f, "to": t, "faces": faces}
    write(A / "blockstates/hermetic_vessel.json", {"variants": {"": {"model": f"{NS}:block/hermetic_vessel"}}})
    write(A / "models/block/hermetic_vessel.json", {
        "parent": "minecraft:block/block",
        "textures": {"particle": f"{NS}:block/hermetic_vessel_side", "side": f"{NS}:block/hermetic_vessel_side",
                     "top": f"{NS}:block/hermetic_vessel_top"},
        "elements": [box([3, 0, 3], [13, 2, 13]), box([4, 2, 4], [12, 11, 12]), box([6, 11, 6], [10, 14, 10])]})
    block_item("hermetic_vessel")
    self_drop("hermetic_vessel")

    for name in EXPANSION_ITEMS:
        flat_item(name)
    for kind in TOOLS:
        flat_item(f"arcanium_{kind}", "minecraft:item/handheld")

    # Ore generation
    ore_feature("ore_cinnabar", "cinnabar_ore", "deepslate_cinnabar_ore", 8, 6, -48, 48)
    ore_feature("ore_silver", "silver_ore", "deepslate_silver_ore", 8, 5, -56, 32)
    ore_feature("ore_lead", "lead_ore", "deepslate_lead_ore", 9, 7, -32, 64)
    ore_feature("ore_rock_salt", "rock_salt", None, 16, 2, 40, 96, "uniform")

    # Smelting
    for metal in ("silver", "lead"):
        for kind, ticks in (("smelting", 200), ("blasting", 100)):
            for src in (f"raw_{metal}", f"{metal}_ore", f"deepslate_{metal}_ore"):
                write(D / f"recipe/{metal}_ingot_from_{kind}_{src}.json", {
                    "type": f"minecraft:{kind}", "cookingtime": ticks, "experience": 0.7, "group": f"{metal}_ingot",
                    "ingredient": f"{NS}:{src}", "result": {"id": f"{NS}:{metal}_ingot"}})
        write(D / f"recipe/{metal}_block.json", {"type": "minecraft:crafting_shaped", "pattern": ["###", "###", "###"],
                                               "key": {"#": f"{NS}:{metal}_ingot"}, "result": {"id": f"{NS}:{metal}_block"}})
        write(D / f"recipe/{metal}_ingot_from_block.json", {"type": "minecraft:crafting_shapeless",
                                                          "ingredients": [f"{NS}:{metal}_block"],
                                                          "result": {"id": f"{NS}:{metal}_ingot", "count": 9}})

    # Crafting
    shaped("alembic", [" C ", "GBG", "CCC"], {"C": "minecraft:copper_ingot", "G": "minecraft:glass", "B": "minecraft:glass_bottle"}, "alembic")
    shaped("hermetic_vessel", ["LGL", "G G", "LAL"], {"L": f"{NS}:lead_ingot", "G": f"{NS}:alchemical_glass", "A": "minecraft:gold_ingot"}, "hermetic_vessel")
    shaped("salt_lamp", ["SSS", "STS", "SSS"], {"S": f"{NS}:salt", "T": "minecraft:torch"}, "salt_lamp")
    shaped("alchemist_lens", [" G ", "GPG", "S  "], {"G": "minecraft:gold_ingot", "P": "minecraft:glass_pane", "S": f"{NS}:silver_ingot"}, "alchemist_lens")
    shaped("emerald_tablet", ["EEE", "EBE", "EEE"], {"E": "minecraft:emerald", "B": "minecraft:book"}, "emerald_tablet")
    write(D / "recipe/prima_materia.json", {"type": "minecraft:crafting_shapeless",
                                            "ingredients": [f"{NS}:salt", f"{NS}:sulfur", f"{NS}:quicksilver", f"{NS}:perditio_crystal"],
                                            "result": {"id": f"{NS}:prima_materia", "count": 1}})
    write(D / "recipe/gunpowder_from_sulfur.json", {"type": "minecraft:crafting_shapeless",
                                                    "ingredients": [f"{NS}:sulfur", f"{NS}:salt", "minecraft:charcoal"],
                                                    "result": {"id": "minecraft:gunpowder", "count": 2}})
    patterns = {"pickaxe": ["AAA", " S ", " S "], "axe": ["AA", "AS", " S"], "shovel": ["A", "S", "S"],
                "sword": ["A", "A", "S"], "hoe": ["AA", " S", " S"]}
    for kind, pattern in patterns.items():
        shaped(f"arcanium_{kind}", pattern, {"A": f"{NS}:arcanium_ingot", "S": "minecraft:stick"}, f"arcanium_{kind}")

    # Tags
    write(D / "tags/item/arcanium_tool_materials.json", {"values": [f"{NS}:arcanium_ingot"]})
    write(RES / "data/minecraft/tags/block/needs_iron_tool.json",
          {"values": [f"{NS}:{n}" for n in ORES if n != "rock_salt"] + [f"{NS}:silver_block"]})
    write(RES / "data/minecraft/tags/block/needs_stone_tool.json", {"values": [f"{NS}:lead_block"]})
    write(RES / "data/c/tags/block/ores.json", {"values": [f"{NS}:{n}" for n in ORES]})
    write(RES / "data/c/tags/item/ores.json", {"values": [f"{NS}:{n}" for n in ORES]})
    write(RES / "data/c/tags/item/ingots.json", {"values": [f"{NS}:silver_ingot", f"{NS}:lead_ingot", f"{NS}:arcanium_ingot"]})
    tool_tags = {"pickaxes": "pickaxe", "axes": "axe", "shovels": "shovel", "swords": "sword", "hoes": "hoe"}
    for tag, kind in tool_tags.items():
        write(RES / f"data/minecraft/tags/item/{tag}.json", {"values": [f"{NS}:arcanium_{kind}"]})
    return list(ORES) + ["silver_block", "lead_block", "salt_lamp", "alembic", "hermetic_vessel"]


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

    # Crafting recipes
    def shaped(name, pattern, key, result, count=1):
        write(D / f"recipe/{name}.json", {"type": "minecraft:crafting_shaped", "category": "building",
                                          "pattern": pattern, "key": key,
                                          "result": {"id": f"{NS}:{result}", "count": count}})
    new_blocks = expansion(shaped)

    # Tags
    blocks = [f"{NS}:{n}" for n in ("athanor_bricks", "alchemical_glass", "resolver", "athanor_core", "ritual_altar", "arcane_pedestal")]
    blocks += [f"{NS}:{n}" for n in new_blocks]
    write(RES / "data/minecraft/tags/block/mineable/pickaxe.json", {"values": blocks})
    write(D / "tags/item/aspect_crystals.json", {"values": [f"{NS}:{a}_crystal" for a in ASPECTS + list(COMPOUNDS)]})

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
