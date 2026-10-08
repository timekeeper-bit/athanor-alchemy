package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.CompoundAspect;
import io.github.timekeeperbit.athanor.item.AlchemistLensItem;
import io.github.timekeeperbit.athanor.item.AlkahestItem;
import io.github.timekeeperbit.athanor.item.ArcaniumTools;
import io.github.timekeeperbit.athanor.item.CharmItem;
import io.github.timekeeperbit.athanor.item.DrinkItem;
import io.github.timekeeperbit.athanor.item.EmeraldTabletItem;
import io.github.timekeeperbit.athanor.item.OuroborosRingItem;
import io.github.timekeeperbit.athanor.item.QuicksilverMirrorItem;
import io.github.timekeeperbit.athanor.item.SaltItem;
import io.github.timekeeperbit.athanor.item.GrowthDustItem;
import io.github.timekeeperbit.athanor.item.ItemMagnetItem;
import io.github.timekeeperbit.athanor.item.PhilosophersStoneItem;
import io.github.timekeeperbit.athanor.item.WandItem;
import io.github.timekeeperbit.athanor.item.OreMagnetItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.component.Consumables;
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

	// Ores, metals and the three principles
	public static final Item CINNABAR = register("cinnabar", Item::new, new Item.Properties());
	public static final Item QUICKSILVER = register("quicksilver", Item::new, new Item.Properties());
	public static final Item SALT = register("salt", SaltItem::new, new Item.Properties());
	public static final Item SULFUR = register("sulfur", Item::new, new Item.Properties());
	public static final Item RAW_SILVER = register("raw_silver", Item::new, new Item.Properties());
	public static final Item RAW_LEAD = register("raw_lead", Item::new, new Item.Properties());
	public static final Item SILVER_INGOT = register("silver_ingot", Item::new, new Item.Properties());
	public static final Item LEAD_INGOT = register("lead_ingot", Item::new, new Item.Properties());
	// The Great Work
	public static final Item PRIMA_MATERIA = register("prima_materia", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item NIGREDO = register("nigredo", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item ALBEDO = register("albedo", Item::new, new Item.Properties().rarity(Rarity.UNCOMMON));
	public static final Item CITRINITAS = register("citrinitas", Item::new, new Item.Properties().rarity(Rarity.RARE));
	public static final Item RUBEDO = register("rubedo", Item::new, new Item.Properties().rarity(Rarity.EPIC));
	// Drinks
	public static final Item AQUA_VITAE = register("aqua_vitae", p -> new DrinkItem(p, (level, entity) -> {
		entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
		entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 600, 0));
		entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 160, 0));
	}, "tooltip.athanor.aqua_vitae"), drink().stacksTo(16));
	public static final Item ELIXIR_OF_LIFE = register("elixir_of_life", p -> new DrinkItem(p, (level, entity) -> {
		entity.setHealth(entity.getMaxHealth());
		entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
		entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
		entity.addEffect(new MobEffectInstance(ModEffects.AETERNITAS, 12000, 0));
	}, "tooltip.athanor.elixir_of_life"), drink().stacksTo(4).rarity(Rarity.EPIC));
	public static final Item PANACEA = register("panacea", p -> new DrinkItem(p, (level, entity) -> {
		List<Holder<MobEffect>> harmful = new ArrayList<>();
		for (MobEffectInstance instance : entity.getActiveEffects()) {
			if (instance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL) {
				harmful.add(instance.getEffect());
			}
		}
		harmful.forEach(entity::removeEffect);
		entity.addEffect(new MobEffectInstance(ModEffects.PURITY, 6000, 0));
	}, "tooltip.athanor.panacea"), drink().stacksTo(16).rarity(Rarity.RARE));
	// Tools
	public static final Item ALKAHEST = register("alkahest", AlkahestItem::new,
			new Item.Properties().durability(AlkahestItem.DURABILITY).rarity(Rarity.RARE));
	public static final Item ALCHEMIST_LENS = register("alchemist_lens", AlchemistLensItem::new, new Item.Properties().stacksTo(1));
	public static final Item QUICKSILVER_MIRROR = register("quicksilver_mirror", QuicksilverMirrorItem::new,
			new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	public static final Item OUROBOROS_RING = register("ouroboros_ring", OuroborosRingItem::new,
			new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
	public static final Item EMERALD_TABLET = register("emerald_tablet", EmeraldTabletItem::new,
			new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).component(DataComponents.WRITTEN_BOOK_CONTENT, EmeraldTabletItem.content()));
	public static final Item ARCANIUM_PICKAXE = register("arcanium_pickaxe", Item::new,
			new Item.Properties().pickaxe(ArcaniumTools.MATERIAL, 1.0F, -2.8F));
	public static final Item ARCANIUM_AXE = register("arcanium_axe", p -> new AxeItem(ArcaniumTools.MATERIAL, 5.5F, -3.0F, p), new Item.Properties());
	public static final Item ARCANIUM_SHOVEL = register("arcanium_shovel", p -> new ShovelItem(ArcaniumTools.MATERIAL, 1.5F, -3.0F, p), new Item.Properties());
	public static final Item ARCANIUM_SWORD = register("arcanium_sword", Item::new,
			new Item.Properties().sword(ArcaniumTools.MATERIAL, 3.0F, -2.4F));
	public static final Item ARCANIUM_HOE = register("arcanium_hoe", p -> new HoeItem(ArcaniumTools.MATERIAL, -3.0F, 0.0F, p), new Item.Properties());
	public static final Set<Item> ARCANIUM_TOOLS = Set.of(ARCANIUM_PICKAXE, ARCANIUM_AXE, ARCANIUM_SHOVEL, ARCANIUM_SWORD, ARCANIUM_HOE);

	private static Item.Properties drink() {
		return new Item.Properties().component(DataComponents.CONSUMABLE, Consumables.DEFAULT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE);
	}

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
