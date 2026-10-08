package io.github.timekeeperbit.athanor.menu;

import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

final class MenuHelper {
	private MenuHelper() {
	}

	/** A slot that only accepts matching items. */
	static Slot filtered(Container container, int index, int x, int y, Predicate<ItemStack> filter) {
		return new Slot(container, index, x, y) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return filter.test(stack);
			}
		};
	}
}
