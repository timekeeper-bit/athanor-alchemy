package io.github.timekeeperbit.athanor.menu;

import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.block.ResolverBlockEntity;
import io.github.timekeeperbit.athanor.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ResolverMenu extends AbstractContainerMenu {
	public static final int INPUT_X = 26;
	public static final int INPUT_Y = 35;
	public static final int OUTPUT_X = 80;
	public static final int OUTPUT_Y = 26;

	private final Container container;
	private final ContainerData data;

	public ResolverMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(ResolverBlockEntity.SIZE), new SimpleContainerData(2));
	}

	public ResolverMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
		super(ModMenus.RESOLVER, containerId);
		checkContainerSize(container, ResolverBlockEntity.SIZE);
		checkContainerDataCount(data, 2);
		this.container = container;
		this.data = data;
		addSlot(MenuHelper.filtered(container, ResolverBlockEntity.INPUT, INPUT_X, INPUT_Y, AspectTable::has));
		for (int i = 0; i < 8; i++) {
			addSlot(MenuHelper.filtered(container, 1 + i, OUTPUT_X + (i % 4) * 18, OUTPUT_Y + (i / 4) * 18, stack -> false));
		}
		addStandardInventorySlots(inventory, 8, 84);
		addDataSlots(data);
	}

	/** Progress from 0 to 1. */
	public float progress() {
		int total = data.get(1);
		return total <= 0 ? 0 : (float) data.get(0) / total;
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack original = stack.copy();
		int machineSlots = ResolverBlockEntity.SIZE;
		if (index < machineSlots) {
			if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (!AspectTable.has(stack) || !moveItemStackTo(stack, 0, 1, false)) {
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		if (stack.getCount() == original.getCount()) {
			return ItemStack.EMPTY;
		}
		slot.onTake(player, stack);
		return original;
	}
}
