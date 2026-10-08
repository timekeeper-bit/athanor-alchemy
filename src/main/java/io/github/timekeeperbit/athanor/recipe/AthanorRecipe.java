package io.github.timekeeperbit.athanor.recipe;

import io.github.timekeeperbit.athanor.aspect.AspectList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** One catalyst is consumed together with the aspect cost to make the result. */
public record AthanorRecipe(Item catalyst, AspectList cost, Item result, int count, int ticks) {
	public ItemStack resultStack() {
		return new ItemStack(result, count);
	}
}
