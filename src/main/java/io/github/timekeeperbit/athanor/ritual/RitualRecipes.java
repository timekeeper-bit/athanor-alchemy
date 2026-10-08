package io.github.timekeeperbit.athanor.ritual;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.CompoundAspect;
import io.github.timekeeperbit.athanor.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class RitualRecipes {
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
			List<RitualRecipe> list = new ArrayList<>();
			list.add(new RitualRecipe(Items.IRON_INGOT, List.of(praecantatio, praecantatio, potentia, instrumentum), arcanium, 2, 100));
			list.add(new RitualRecipe(Items.DIAMOND, List.of(arcanium, arcanium, Items.GOLD_INGOT, Items.GOLD_INGOT, praecantatio, potentia, anima, lux),
					ModItems.PHILOSOPHERS_STONE, 1, 300));
			list.add(new RitualRecipe(arcanium, List.of(motus, motus, Aspect.AER.crystal(), Aspect.AER.crystal(), Items.FEATHER, Items.SUGAR),
					ModItems.CHARM_SWIFTNESS, 1, 160));
			list.add(new RitualRecipe(arcanium, List.of(anima, anima, Aspect.VITA.crystal(), Aspect.VITA.crystal(), Items.GOLDEN_APPLE, Items.GLISTERING_MELON_SLICE),
					ModItems.CHARM_VITALITY, 1, 200));
			list.add(new RitualRecipe(arcanium, List.of(lux, lux, Items.GOLDEN_CARROT, Items.GOLDEN_CARROT, Items.SPIDER_EYE),
					ModItems.CHARM_NIGHT_VISION, 1, 160));
			list.add(new RitualRecipe(arcanium, List.of(Aspect.AQUA.crystal(), Aspect.AQUA.crystal(), motus, Items.KELP, Items.KELP, Items.PRISMARINE_SHARD),
					ModItems.CHARM_TIDES, 1, 160));
			list.add(new RitualRecipe(arcanium, List.of(Aspect.IGNIS.crystal(), Aspect.IGNIS.crystal(), potentia, Items.MAGMA_CREAM, Items.MAGMA_CREAM, Items.OBSIDIAN),
					ModItems.CHARM_EMBERS, 1, 160));
			list.add(new RitualRecipe(Items.BONE_MEAL, List.of(Aspect.VITA.crystal(), Aspect.VITA.crystal(), anima, Aspect.TERRA.crystal()),
					ModItems.GROWTH_DUST, 8, 80));
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
