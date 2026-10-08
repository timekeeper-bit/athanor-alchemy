package io.github.timekeeperbit.athanor.alembic;

import io.github.timekeeperbit.athanor.registry.ModBlocks;
import io.github.timekeeperbit.athanor.registry.ModItems;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/** The alembic separates matter into the three principles: Salt, Sulphur and Mercury, plus Aqua Vitae from ferments. */
public final class AlembicRecipes {
	private static List<AlembicRecipe> recipes;

	private AlembicRecipes() {
	}

	public static List<AlembicRecipe> all() {
		if (recipes == null) {
			List<AlembicRecipe> list = new ArrayList<>();
			// Cinnabar is mercury sulphide: roasting it frees quicksilver and leaves sulphur.
			add(list, ModItems.CINNABAR, ModItems.QUICKSILVER, 1, ModItems.SULFUR, 1);
			add(list, Items.REDSTONE, ModItems.QUICKSILVER, 1, null, 0);
			add(list, Items.COAL, ModItems.SULFUR, 1, null, 0);
			add(list, Items.BLAZE_POWDER, ModItems.SULFUR, 2, null, 0);
			add(list, Items.GUNPOWDER, ModItems.SULFUR, 1, ModItems.SALT, 1);
			add(list, Items.KELP, ModItems.SALT, 1, null, 0);
			add(list, Items.DRIED_KELP_BLOCK, ModItems.SALT, 6, null, 0);
			add(list, Items.BONE, ModItems.SALT, 1, Items.BONE_MEAL, 1);
			add(list, ModBlocks.ROCK_SALT.asItem(), ModItems.SALT, 4, null, 0);
			add(list, Items.APPLE, ModItems.AQUA_VITAE, 1, null, 0);
			add(list, Items.SWEET_BERRIES, ModItems.AQUA_VITAE, 1, null, 0);
			add(list, Items.WHEAT, ModItems.AQUA_VITAE, 1, null, 0);
			add(list, Items.ROTTEN_FLESH, ModItems.SALT, 1, Items.LEATHER, 1);
			recipes = Collections.unmodifiableList(list);
		}
		return recipes;
	}

	public static AlembicRecipe find(ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}
		for (AlembicRecipe recipe : all()) {
			if (stack.is(recipe.input())) {
				return recipe;
			}
		}
		return null;
	}

	private static void add(List<AlembicRecipe> list, Item input, Item product, int count, Item byproduct, int byproductCount) {
		list.add(new AlembicRecipe(input, product, count, byproduct, byproductCount));
	}
}
