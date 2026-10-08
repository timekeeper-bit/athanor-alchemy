package io.github.timekeeperbit.athanor.ritual;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.CompoundAspect;
import io.github.timekeeperbit.athanor.registry.ModItems;
import io.github.timekeeperbit.athanor.world.Celestial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class RitualRecipes {
	/** The most aether an ordinary ritual may need: the lowest base any chunk has. */
	public static final int MAX_ORDINARY_AETHER = 70;
	/** Aether for the grand rite: more than any ordinary chunk's base, less than a ley node's. */
	public static final int GRAND_RITE_AETHER = 150;
	private static final Celestial.Time ANY = Celestial.Time.ANY;
	private static final Celestial.Time DAY = Celestial.Time.DAY;
	private static final Celestial.Time NIGHT = Celestial.Time.NIGHT;
	private static List<RitualRecipe> recipes;

	private RitualRecipes() {
	}

	public static List<RitualRecipe> all() {
		if (recipes == null) {
			Item lux = CompoundAspect.LUX.crystal();
			Item potentia = CompoundAspect.POTENTIA.crystal();
			Item motus = CompoundAspect.MOTUS.crystal();
			Item praecantatio = CompoundAspect.PRAECANTATIO.crystal();
			Item anima = CompoundAspect.ANIMA.crystal();
			Item instrumentum = CompoundAspect.INSTRUMENTUM.crystal();
			Item arcanium = ModItems.ARCANIUM_INGOT;
			Item vita = Aspect.VITA.crystal();
			Item perditio = Aspect.PERDITIO.crystal();
			List<RitualRecipe> list = new ArrayList<>();
			list.add(new RitualRecipe(Items.IRON_INGOT, List.of(praecantatio, praecantatio, potentia, instrumentum), arcanium, 2, 100, 10, ANY));
			list.add(new RitualRecipe(Items.DIAMOND, List.of(arcanium, arcanium, Items.GOLD_INGOT, Items.GOLD_INGOT, praecantatio, potentia, anima, lux),
					ModItems.PHILOSOPHERS_STONE, 1, 300, 70, ANY));
			list.add(new RitualRecipe(arcanium, List.of(motus, motus, Aspect.AER.crystal(), Aspect.AER.crystal(), Items.FEATHER, Items.SUGAR),
					ModItems.CHARM_SWIFTNESS, 1, 160, 25, ANY));
			list.add(new RitualRecipe(arcanium, List.of(anima, anima, vita, vita, Items.GOLDEN_APPLE, Items.GLISTERING_MELON_SLICE),
					ModItems.CHARM_VITALITY, 1, 200, 25, ANY));
			list.add(new RitualRecipe(arcanium, List.of(lux, lux, Items.GOLDEN_CARROT, Items.GOLDEN_CARROT, Items.SPIDER_EYE),
					ModItems.CHARM_NIGHT_VISION, 1, 160, 25, NIGHT));
			list.add(new RitualRecipe(arcanium, List.of(Aspect.AQUA.crystal(), Aspect.AQUA.crystal(), motus, Items.KELP, Items.KELP, Items.PRISMARINE_SHARD),
					ModItems.CHARM_TIDES, 1, 160, 25, ANY));
			list.add(new RitualRecipe(arcanium, List.of(Aspect.IGNIS.crystal(), Aspect.IGNIS.crystal(), potentia, Items.MAGMA_CREAM, Items.MAGMA_CREAM, Items.OBSIDIAN),
					ModItems.CHARM_EMBERS, 1, 160, 25, DAY));
			list.add(new RitualRecipe(Items.BONE_MEAL, List.of(vita, vita, anima, Aspect.TERRA.crystal()),
					ModItems.GROWTH_DUST, 8, 80, 5, ANY));
			// Magnum Opus
			list.add(new RitualRecipe(Items.GLASS_BOTTLE, List.of(ModItems.QUICKSILVER, ModItems.SULFUR, ModItems.SALT, perditio, perditio, praecantatio),
					ModItems.ALKAHEST, 1, 160, 30, ANY));
			list.add(new RitualRecipe(ModItems.ALBEDO, List.of(ModItems.AQUA_VITAE, ModItems.SALT, ModItems.SALT, Items.HONEY_BOTTLE, vita, Items.GOLDEN_CARROT),
					ModItems.PANACEA, 3, 200, 40, ANY));
			list.add(new RitualRecipe(ModItems.SILVER_INGOT, List.of(ModItems.QUICKSILVER, ModItems.QUICKSILVER, Items.ENDER_PEARL, Items.ENDER_PEARL, motus, motus),
					ModItems.QUICKSILVER_MIRROR, 1, 200, 40, NIGHT));
			list.add(new RitualRecipe(ModItems.CITRINITAS, List.of(arcanium, arcanium, Items.EXPERIENCE_BOTTLE, Items.EXPERIENCE_BOTTLE, potentia, instrumentum, Items.EMERALD),
					ModItems.OUROBOROS_RING, 1, 240, 60, ANY));
			list.add(new RitualRecipe(ModItems.RUBEDO, List.of(Items.GOLDEN_APPLE, ModItems.AQUA_VITAE, ModItems.AQUA_VITAE, anima, anima, vita, vita, Items.GLISTERING_MELON_SLICE),
					ModItems.ELIXIR_OF_LIFE, 1, 300, 70, NIGHT));
			// The grand rite needs more aether than ordinary land holds: it can only be performed on a ley node.
			list.add(new RitualRecipe(ModItems.RUBEDO, List.of(Items.GOLD_INGOT, Items.GOLD_INGOT, ModItems.SILVER_INGOT, ModItems.SILVER_INGOT,
					ModItems.QUICKSILVER, ModItems.SULFUR, ModItems.SALT, praecantatio), ModItems.PHILOSOPHERS_STONE, 2, 400, GRAND_RITE_AETHER, DAY));
			recipes = Collections.unmodifiableList(list);
		}
		return recipes;
	}

	public static RitualRecipe find(ItemStack center, List<ItemStack> pedestals) {
		for (RitualRecipe recipe : all()) {
			if (recipe.matches(center, pedestals)) {
				return recipe;
			}
		}
		return null;
	}
}
