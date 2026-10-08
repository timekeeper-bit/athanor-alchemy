package io.github.timekeeperbit.athanor.menu;

import io.github.timekeeperbit.athanor.alembic.AlembicBlockEntity;
import io.github.timekeeperbit.athanor.alembic.AlembicRecipes;
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

public class AlembicMenu extends AbstractContainerMenu {
	public static final int INPUT_X = 44;
	public static final int INPUT_Y = 26;
	public static final int PRODUCT_X = 108;
	public static final int BYPRODUCT_X = 134;
	public static final int OUTPUT_Y = 35;
	public static final int HEAT_X = 44;
	public static final int HEAT_Y = 50;

	private final Container container;
	private final ContainerData data;

	public AlembicMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(AlembicBlockEntity.SIZE), new SimpleContainerData(3));
	}

	public AlembicMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
		super(ModMenus.ALEMBIC, containerId);
		checkContainerSize(container, AlembicBlockEntity.SIZE);
		checkContainerDataCount(data, 3);
		this.container = container;
		this.data = data;
		addSlot(MenuHelper.filtered(container, AlembicBlockEntity.INPUT, INPUT_X, INPUT_Y, stack -> AlembicRecipes.find(stack) != null));
		addSlot(MenuHelper.filtered(container, AlembicBlockEntity.PRODUCT, PRODUCT_X, OUTPUT_Y, stack -> false));
		addSlot(MenuHelper.filtered(container, AlembicBlockEntity.BYPRODUCT, BYPRODUCT_X, OUTPUT_Y, stack -> false));
		addStandardInventorySlots(inventory, 8, 84);
		addDataSlots(data);
	}

	public float progress() {
		int total = data.get(1);
		return total <= 0 ? 0 : (float) data.get(0) / total;
	}

	public boolean isHeated() {
		return data.get(2) != 0;
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
		int machineSlots = AlembicBlockEntity.SIZE;
		if (index < machineSlots) {
			if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (AlembicRecipes.find(stack) == null || !moveItemStackTo(stack, 0, 1, false)) {
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
