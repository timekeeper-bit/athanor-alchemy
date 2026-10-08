package io.github.timekeeperbit.athanor.ritual;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** An infusion ritual: the centre item on the altar plus every pedestal item (in any order) becomes the result. */
public record RitualRecipe(Item center, List<Item> pedestals, Item result, int count, int ticks) {
	public ItemStack resultStack() {
		return new ItemStack(result, count);
	}

	/** True when the given pedestal items are exactly this recipe's ingredients, ignoring order and empty pedestals. */
	public boolean matches(ItemStack centerStack, List<ItemStack> pedestalStacks) {
		if (!centerStack.is(center)) {
			return false;
		}
		List<Item> remaining = new ArrayList<>(pedestals);
		for (ItemStack stack : pedestalStacks) {
			if (stack.isEmpty()) {
				continue;
			}
			if (!remaining.remove(stack.getItem())) {
				return false;
			}
		}
		return remaining.isEmpty();
	}
}
