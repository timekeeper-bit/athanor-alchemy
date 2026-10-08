package io.github.timekeeperbit.athanor.block;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.AspectList;
import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.menu.ResolverMenu;
import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import io.github.timekeeperbit.athanor.world.Aura;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Breaks one item at a time into aspect crystals. Slot 0 is the input, slots 1-8 hold one aspect each. */
public class ResolverBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
	public static final int INPUT = 0;
	public static final int SIZE = 1 + Aspect.COUNT;
	public static final int WORK_TICKS = 80;
	private static final int[] TOP_SLOTS = {INPUT};
	private static final int[] OUTPUT_SLOTS = {1, 2, 3, 4, 5, 6, 7, 8};

	private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	private int progress;

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return index == 0 ? progress : WORK_TICKS;
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				progress = value;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
	};

	public ResolverBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RESOLVER, pos, state);
	}

	public static int slotFor(Aspect aspect) {
		return 1 + aspect.ordinal();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ResolverBlockEntity resolver) {
		resolver.tick();
	}

	void tick() {
		ItemStack input = items.get(INPUT);
		AspectList aspects = AspectTable.get(input);
		if (aspects.isEmpty() || !canFit(aspects)) {
			if (progress != 0) {
				progress = 0;
				setChanged();
			}
			return;
		}
		progress++;
		if (progress >= WORK_TICKS) {
			progress = 0;
			for (Aspect aspect : Aspect.values()) {
				int amount = aspects.get(aspect);
				if (amount > 0) {
					ItemStack out = items.get(slotFor(aspect));
					if (out.isEmpty()) {
						items.set(slotFor(aspect), new ItemStack(aspect.crystal(), amount));
					} else {
						out.grow(amount);
					}
				}
			}
			input.shrink(1);
			if (level instanceof ServerLevel serverLevel) {
				// Tearing matter apart leaves miasma behind.
				Aura.addMiasma(serverLevel, getBlockPos(), 1);
			}
		}
		setChanged();
	}

	private boolean canFit(AspectList aspects) {
		for (Aspect aspect : Aspect.values()) {
			int amount = aspects.get(aspect);
			if (amount > 0) {
				ItemStack out = items.get(slotFor(aspect));
				if (!out.isEmpty() && out.getCount() + amount > out.getMaxStackSize()) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items.clear();
		ContainerHelper.loadAllItems(input, items);
		progress = input.getIntOr("Progress", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.putInt("Progress", progress);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			Containers.dropContents(level, pos, this);
		}
		super.preRemoveSideEffects(pos, state);
	}

	// Container

	@Override
	public int getContainerSize() {
		return SIZE;
	}

	@Override
	public boolean isEmpty() {
		return items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int slot) {
		return items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack removed = ContainerHelper.removeItem(items, slot, amount);
		if (!removed.isEmpty()) {
			setChanged();
		}
		return removed;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		return ContainerHelper.takeItem(items, slot);
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		items.set(slot, stack);
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return net.minecraft.world.Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		if (slot == INPUT) {
			return AspectTable.has(stack);
		}
		return Aspect.fromCrystal(stack.getItem()) != null && slot == slotFor(Aspect.fromCrystal(stack.getItem()));
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.UP ? TOP_SLOTS : OUTPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return slot == INPUT && AspectTable.has(stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return slot != INPUT;
	}

	// Menu

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.athanor.resolver");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new ResolverMenu(containerId, inventory, this, data);
	}
}
