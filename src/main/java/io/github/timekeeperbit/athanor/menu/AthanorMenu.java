package io.github.timekeeperbit.athanor.menu;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlockEntity;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipe;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipes;
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

public class AthanorMenu extends AbstractContainerMenu {
	public static final int ESSENCE_X = 108;
	public static final int ESSENCE_Y = 20;
	public static final int CATALYST_X = 108;
	public static final int CATALYST_Y = 54;
	public static final int OUTPUT_X = 152;
	public static final int OUTPUT_Y = 54;
	public static final int INVENTORY_Y = 102;

	private final Container container;
	private final ContainerData data;

	public AthanorMenu(int containerId, Inventory inventory) {
		this(containerId, inventory, new SimpleContainer(AthanorCoreBlockEntity.SIZE), new SimpleContainerData(AthanorCoreBlockEntity.DATA_COUNT));
	}

	public AthanorMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
		super(ModMenus.ATHANOR, containerId);
		checkContainerSize(container, AthanorCoreBlockEntity.SIZE);
		checkContainerDataCount(data, AthanorCoreBlockEntity.DATA_COUNT);
		this.container = container;
		this.data = data;
		addSlot(MenuHelper.filtered(container, AthanorCoreBlockEntity.ESSENCE, ESSENCE_X, ESSENCE_Y, stack -> Aspect.fromCrystal(stack.getItem()) != null));
		addSlot(new Slot(container, AthanorCoreBlockEntity.CATALYST, CATALYST_X, CATALYST_Y));
		addSlot(MenuHelper.filtered(container, AthanorCoreBlockEntity.OUTPUT, OUTPUT_X, OUTPUT_Y, stack -> false));
		addStandardInventorySlots(inventory, 8, INVENTORY_Y);
		addDataSlots(data);
	}

	public int pool(Aspect aspect) {
		return data.get(aspect.ordinal());
	}

	public boolean isFormed() {
		return data.get(AthanorCoreBlockEntity.DATA_FORMED) != 0;
	}

	public float progress() {
		int total = data.get(AthanorCoreBlockEntity.DATA_TOTAL);
		return total <= 0 ? 0 : (float) data.get(AthanorCoreBlockEntity.DATA_PROGRESS) / total;
	}

	/** Recipe for the catalyst currently in the menu, or null. Works on both sides because recipes are static. */
	public AthanorRecipe currentRecipe() {
		return AthanorRecipes.find(slots.get(AthanorCoreBlockEntity.CATALYST).getItem());
	}

	public boolean hasCatalyst() {
		return slots.get(AthanorCoreBlockEntity.CATALYST).hasItem();
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
		int machineSlots = AthanorCoreBlockEntity.SIZE;
		if (index < machineSlots) {
			if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (Aspect.fromCrystal(stack.getItem()) != null) {
			if (!moveItemStackTo(stack, AthanorCoreBlockEntity.ESSENCE, AthanorCoreBlockEntity.ESSENCE + 1, false)) {
				return ItemStack.EMPTY;
			}
		} else if (!moveItemStackTo(stack, AthanorCoreBlockEntity.CATALYST, AthanorCoreBlockEntity.CATALYST + 1, false)) {
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
