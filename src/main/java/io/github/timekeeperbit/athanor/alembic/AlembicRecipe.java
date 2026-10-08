package io.github.timekeeperbit.athanor.alembic;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Distils one input item into a product and, for some inputs, a by-product. */
public record AlembicRecipe(Item input, Item product, int count, Item byproduct, int byproductCount) {
	public ItemStack productStack() {
		return new ItemStack(product, count);
	}

	public ItemStack byproductStack() {
		return byproduct == null ? ItemStack.EMPTY : new ItemStack(byproduct, byproductCount);
	}
}
