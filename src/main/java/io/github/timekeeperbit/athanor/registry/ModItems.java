package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.item.ItemMagnetItem;
import io.github.timekeeperbit.athanor.item.OreMagnetItem;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
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

	public static void init() {
	}
}
