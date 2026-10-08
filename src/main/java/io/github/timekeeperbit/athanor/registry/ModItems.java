package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.CompoundAspect;
import io.github.timekeeperbit.athanor.item.CharmItem;
import io.github.timekeeperbit.athanor.item.GrowthDustItem;
import io.github.timekeeperbit.athanor.item.ItemMagnetItem;
import io.github.timekeeperbit.athanor.item.PhilosophersStoneItem;
import io.github.timekeeperbit.athanor.item.WandItem;
import io.github.timekeeperbit.athanor.item.OreMagnetItem;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModItems {
	public static final Map<Aspect, Item> CRYSTALS;

	static {
		Map<Aspect, Item> crystals = new EnumMap<>(Aspect.class);
		for (Aspect aspect : Aspect.values()) {
			crystals.put(aspect, register(aspect.getName() + "_crystal", Item::new, new Item.Properties()));
		}
		CRYSTALS = Collections.unmodifiableMap(crystals);
	}

	public static final Map<CompoundAspect, Item> COMPOUND_CRYSTALS;

	static {
		Map<CompoundAspect, Item> crystals = new EnumMap<>(CompoundAspect.class);
		for (CompoundAspect aspect : CompoundAspect.values()) {
			crystals.put(aspect, register(aspect.getName() + "_crystal", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON)));
		}
		COMPOUND_CRYSTALS = Collections.unmodifiableMap(crystals);
	}

	public static final Item WAND = register("alchemist_wand", WandItem::new, new Item.Properties().stacksTo(1));
	public static final Item ARCANIUM_INGOT = register("arcanium_ingot", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item PHILOSOPHERS_STONE = register("philosophers_stone", PhilosophersStoneItem::new,
			new Item.Properties().durability(PhilosophersStoneItem.DURABILITY).rarity(Rarity.EPIC));
	public static final Item GROWTH_DUST = register("growth_dust", GrowthDustItem::new, new Item.Properties());
	public static final Item CHARM_SWIFTNESS = charm("charm_swiftness", MobEffects.SPEED, 1, false);
	public static final Item CHARM_VITALITY = charm("charm_vitality", MobEffects.REGENERATION, 0, true);
	public static final Item CHARM_NIGHT_VISION = charm("charm_night_vision", MobEffects.NIGHT_VISION, 0, false);
	public static final Item CHARM_TIDES = charm("charm_tides", MobEffects.WATER_BREATHING, 0, false);
	public static final Item CHARM_EMBERS = charm("charm_embers", MobEffects.FIRE_RESISTANCE, 0, false);
	public static final Item ORE_MAGNET = register("ore_magnet", OreMagnetItem::new,
			new Item.Properties().durability(OreMagnetItem.DURABILITY).rarity(Rarity.UNCOMMON));
	public static final Item ITEM_MAGNET = register("item_magnet", ItemMagnetItem::new,
			new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));

	private ModItems() {
	}

	private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties properties) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Athanor.id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(properties.setId(key)));
	}

	private static Item charm(String name, Holder<MobEffect> effect, int amplifier, boolean onlyWhenHurt) {
		return register(name, properties -> new CharmItem(properties, effect, amplifier, onlyWhenHurt),
				new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}

	public static void init() {
	}
}
