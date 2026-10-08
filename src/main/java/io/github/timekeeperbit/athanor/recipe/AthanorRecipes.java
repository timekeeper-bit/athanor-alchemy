package io.github.timekeeperbit.athanor.recipe;

import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** All Athanor recipes, keyed by catalyst. Each catalyst has at most one recipe. */
public final class AthanorRecipes {
	private static List<AthanorRecipe> recipes;

	private AthanorRecipes() {
	}

	public static List<AthanorRecipe> all() {
		if (recipes == null) {
			List<AthanorRecipe> list = new ArrayList<>();
			// Tools
			add(list, Items.IRON_PICKAXE, "M16 T8 O8", ModItems.ORE_MAGNET, 1, 200);
			add(list, Items.COMPASS, "M8 E8 O4", ModItems.ITEM_MAGNET, 1, 200);
			// Multiplication
			add(list, Items.IRON_INGOT, "M4 O2", Items.IRON_INGOT, 2, 100);
			add(list, Items.COPPER_INGOT, "M3 T2", Items.COPPER_INGOT, 2, 80);
			add(list, Items.GOLD_INGOT, "M4 O4", Items.GOLD_INGOT, 2, 120);
			add(list, Items.DIAMOND, "O12 T8", Items.DIAMOND, 2, 400);
			add(list, Items.EMERALD, "O10 V6", Items.EMERALD, 2, 300);
			add(list, Items.QUARTZ, "O4", Items.QUARTZ, 2, 80);
			add(list, Items.ENDER_PEARL, "E5 O2 P2", Items.ENDER_PEARL, 2, 160);
			add(list, Items.NETHERITE_SCRAP, "M10 I4 P4", Items.NETHERITE_SCRAP, 2, 600);
			add(list, Items.OBSIDIAN, "T3 I2 O3", Items.OBSIDIAN, 2, 100);
			// Transmutation
			add(list, Items.GLASS_BOTTLE, "V4 O2", Items.EXPERIENCE_BOTTLE, 1, 80);
			add(list, Items.ROTTEN_FLESH, "O2 V1", Items.LEATHER, 1, 60);
			add(list, Items.CLAY_BALL, "V2 A2", Items.SLIME_BALL, 1, 80);
			add(list, Items.REDSTONE, "I2 E2 O1", Items.GLOWSTONE_DUST, 2, 80);
			add(list, Items.BLAZE_POWDER, "I4 O2", Items.BLAZE_ROD, 1, 120);
			add(list, Items.PAPER, "O4 E2", Items.NAME_TAG, 1, 120);
			add(list, Items.CHARCOAL, "T1 P1", Items.COAL, 1, 40);
			// Metals and principles
			add(list, ModItems.LEAD_INGOT, "M4 O4 I2", Items.GOLD_INGOT, 1, 200);
			add(list, ModItems.SILVER_INGOT, "M4 O2", ModItems.SILVER_INGOT, 2, 120);
			add(list, ModItems.QUICKSILVER, "M2 A1", ModItems.QUICKSILVER, 2, 80);
			add(list, ModItems.SALT, "T2 O2", ModItems.SALT, 2, 60);
			add(list, ModItems.SULFUR, "I3 P3 E3", Items.GUNPOWDER, 2, 80);
			add(list, ModItems.CINNABAR, "I3 O3", Items.REDSTONE, 4, 80);
			recipes = Collections.unmodifiableList(list);
		}
		return recipes;
	}

	public static AthanorRecipe find(ItemStack catalyst) {
		if (catalyst.isEmpty()) {
			return null;
		}
		for (AthanorRecipe recipe : all()) {
			if (catalyst.is(recipe.catalyst())) {
				return recipe;
			}
		}
		return null;
	}

	private static void add(List<AthanorRecipe> list, Item catalyst, String cost, Item result, int count, int ticks) {
		list.add(new AthanorRecipe(catalyst, AspectTable.of(cost), result, count, ticks));
	}
}
