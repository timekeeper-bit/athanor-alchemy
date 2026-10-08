package io.github.timekeeperbit.athanor.block;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.menu.AthanorMenu;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipe;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipes;
import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

/**
 * Controller of the Athanor multiblock. Crystals put in slot 0 are absorbed into the aspect pool; a catalyst in slot 1
 * is turned into the recipe result in slot 2, consuming aspects from the pool.
 */
public class AthanorCoreBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
	public static final int ESSENCE = 0;
	public static final int CATALYST = 1;
	public static final int OUTPUT = 2;
	public static final int SIZE = 3;
	public static final int POOL_CAPACITY = 512;
	public static final int CHECK_INTERVAL = 20;
	/** Synced data: 0-7 pool, 8 progress, 9 total ticks, 10 formed. */
	public static final int DATA_PROGRESS = 8;
	public static final int DATA_TOTAL = 9;
	public static final int DATA_FORMED = 10;
	public static final int DATA_COUNT = 11;

	private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	private final int[] pool = new int[Aspect.COUNT];
	private int progress;
	private int totalTicks;
	private boolean formed;
	private int checkTimer;

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int index) {
			if (index < Aspect.COUNT) {
				return pool[index];
			}
			return switch (index) {
				case DATA_PROGRESS -> progress;
				case DATA_TOTAL -> totalTicks;
				case DATA_FORMED -> formed ? 1 : 0;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
		}

		@Override
		public int getCount() {
			return DATA_COUNT;
		}
	};

	public AthanorCoreBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ATHANOR_CORE, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, AthanorCoreBlockEntity core) {
		core.tick((ServerLevel) level, pos, state);
	}

	public int getPool(Aspect aspect) {
		return pool[aspect.ordinal()];
	}

	public void setPool(Aspect aspect, int amount) {
		pool[aspect.ordinal()] = Math.max(0, Math.min(POOL_CAPACITY, amount));
		setChanged();
	}

	public boolean isFormed() {
		return formed;
	}

	private void tick(ServerLevel level, BlockPos pos, BlockState state) {
		if (--checkTimer <= 0) {
			checkTimer = CHECK_INTERVAL;
			updateFormed(level, pos, state);
		}
		absorbEssence();

		AthanorRecipe recipe = AthanorRecipes.find(items.get(CATALYST));
		if (!formed || recipe == null || !hasAspects(recipe) || !canOutput(recipe)) {
			if (progress != 0) {
				progress = 0;
				setChanged();
			}
			totalTicks = recipe == null ? 0 : recipe.ticks();
			return;
		}
		totalTicks = recipe.ticks();
		progress++;
		if (progress % 10 == 0) {
			level.sendParticles(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.3, 0.1, 0.3, 0.0);
		}
		if (progress >= recipe.ticks()) {
			progress = 0;
			craft(recipe);
			level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.8F, 1.0F);
			level.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 20, 0.4, 0.4, 0.4, 0.5);
		}
		setChanged();
	}

	private void updateFormed(ServerLevel level, BlockPos pos, BlockState state) {
		boolean nowFormed = AthanorStructure.findProblem(level, pos, state.getValue(AthanorCoreBlock.FACING)) == null;
		if (nowFormed != formed) {
			formed = nowFormed;
			level.setBlockAndUpdate(pos, state.setValue(AthanorCoreBlock.FORMED, nowFormed));
			if (nowFormed) {
				level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 0.6F, 1.4F);
			}
			setChanged();
		}
	}

	private void absorbEssence() {
		ItemStack stack = items.get(ESSENCE);
		Aspect aspect = Aspect.fromCrystal(stack.getItem());
		if (stack.isEmpty() || aspect == null) {
			return;
		}
		int room = POOL_CAPACITY - pool[aspect.ordinal()];
		int moved = Math.min(room, stack.getCount());
		if (moved > 0) {
			pool[aspect.ordinal()] += moved;
			stack.shrink(moved);
			setChanged();
		}
	}

	private boolean hasAspects(AthanorRecipe recipe) {
		for (Aspect aspect : Aspect.values()) {
			if (pool[aspect.ordinal()] < recipe.cost().get(aspect)) {
				return false;
			}
		}
		return true;
	}

	private boolean canOutput(AthanorRecipe recipe) {
		ItemStack out = items.get(OUTPUT);
		ItemStack result = recipe.resultStack();
		return out.isEmpty()
				|| ItemStack.isSameItemSameComponents(out, result) && out.getCount() + result.getCount() <= out.getMaxStackSize();
	}

	private void craft(AthanorRecipe recipe) {
		for (Aspect aspect : Aspect.values()) {
			pool[aspect.ordinal()] -= recipe.cost().get(aspect);
		}
		items.get(CATALYST).shrink(1);
		ItemStack out = items.get(OUTPUT);
		if (out.isEmpty()) {
			items.set(OUTPUT, recipe.resultStack());
		} else {
			out.grow(recipe.count());
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		items.clear();
		ContainerHelper.loadAllItems(input, items);
		int[] saved = input.getIntArray("Pool").orElse(new int[0]);
		for (int i = 0; i < pool.length; i++) {
			pool[i] = i < saved.length ? saved[i] : 0;
		}
		progress = input.getIntOr("Progress", 0);
		formed = input.getBooleanOr("Formed", false);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, items);
		output.putIntArray("Pool", pool.clone());
		output.putInt("Progress", progress);
		output.putBoolean("Formed", formed);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			Containers.dropContents(level, pos, this);
			// Return the stored aspects as crystals so nothing is lost.
			for (Aspect aspect : Aspect.values()) {
				int left = pool[aspect.ordinal()];
				while (left > 0) {
					int count = Math.min(64, left);
					Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(aspect.crystal(), count));
					left -= count;
				}
				pool[aspect.ordinal()] = 0;
			}
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
		return switch (slot) {
			case ESSENCE -> Aspect.fromCrystal(stack.getItem()) != null;
			case CATALYST -> true;
			default -> false;
		};
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return switch (side) {
			case UP -> new int[] {ESSENCE};
			case DOWN -> new int[] {OUTPUT};
			default -> new int[] {CATALYST};
		};
	}

	@Override
	public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
		return canPlaceItem(slot, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
		return slot == OUTPUT;
	}

	// Menu

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.athanor.athanor");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new AthanorMenu(containerId, inventory, this, data);
	}
}
