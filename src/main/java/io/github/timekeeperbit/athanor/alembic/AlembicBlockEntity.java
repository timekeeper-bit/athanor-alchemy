package io.github.timekeeperbit.athanor.alembic;

import io.github.timekeeperbit.athanor.menu.AlembicMenu;
import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import io.github.timekeeperbit.athanor.world.Heat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
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

/** Distils the input over a heat source below it. Slot 0 is the input, 1 the product, 2 the by-product. */
public class AlembicBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
	public static final int INPUT = 0;
	public static final int PRODUCT = 1;
	public static final int BYPRODUCT = 2;
	public static final int SIZE = 3;
	public static final int WORK_TICKS = 100;
	private static final int[] TOP_SLOTS = {INPUT};
	private static final int[] OUTPUT_SLOTS = {PRODUCT, BYPRODUCT};

	private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	private int progress;
	private boolean heated;

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> progress;
				case 1 -> WORK_TICKS;
				default -> heated ? 1 : 0;
			};
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				progress = value;
			} else if (index == 2) {
				heated = value != 0;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}
	};

	public AlembicBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ALEMBIC, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AlembicBlockEntity alembic) {
		alembic.tick((ServerLevel) level, pos);
	}

	public boolean isHeated() {
		return heated;
	}

	private void tick(ServerLevel level, BlockPos pos) {
		heated = Heat.below(level, pos);
		AlembicRecipe recipe = AlembicRecipes.find(items.get(INPUT));
		if (!heated || recipe == null || !fits(PRODUCT, recipe.productStack()) || !fits(BYPRODUCT, recipe.byproductStack())) {
			if (progress != 0) {
				progress = 0;
				setChanged();
			}
			return;
		}
		progress++;
		if (progress % 20 == 0) {
			level.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, 1, 0.1, 0.0, 0.1, 0.01);
		}
		if (progress >= WORK_TICKS) {
			progress = 0;
			insert(PRODUCT, recipe.productStack());
			insert(BYPRODUCT, recipe.byproductStack());
			items.get(INPUT).shrink(1);
			level.sendParticles(ParticleTypes.DRIPPING_WATER, pos.getX() + 0.8, pos.getY() + 0.6, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0.0);
		}
		setChanged();
	}

	private boolean fits(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}
		ItemStack current = items.get(slot);
		return current.isEmpty()
				|| ItemStack.isSameItemSameComponents(current, stack) && current.getCount() + stack.getCount() <= current.getMaxStackSize();
	}

	private void insert(int slot, ItemStack stack) {
		if (stack.isEmpty()) {
			return;
		}
		ItemStack current = items.get(slot);
		if (current.isEmpty()) {
			items.set(slot, stack);
		} else {
			current.grow(stack.getCount());
		}
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
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot == INPUT && AlembicRecipes.find(stack) != null;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return side == Direction.UP ? TOP_SLOTS : OUTPUT_SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return slot != INPUT;
	}

	// Menu

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.athanor.alembic");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new AlembicMenu(containerId, inventory, this, data);
	}
}
