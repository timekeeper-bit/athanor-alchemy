package io.github.timekeeperbit.athanor.ritual;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

/**
 * Holds a single item and shows it as a floating, unpickable item entity above the block. The display entity is
 * re-synchronized once a second, so it survives chunk reloads and never duplicates.
 */
public abstract class ItemHolderBlockEntity extends BlockEntity {
	private static final int SYNC_INTERVAL = 20;

	private ItemStack stack = ItemStack.EMPTY;
	private int syncTimer;

	protected ItemHolderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ItemStack getStack() {
		return stack;
	}

	public void setStack(ItemStack newStack) {
		stack = newStack;
		setChanged();
		syncTimer = 0;
		if (level instanceof ServerLevel serverLevel) {
			syncDisplay(serverLevel);
		}
	}

	/** Removes and returns the held item. */
	public ItemStack takeStack() {
		ItemStack taken = stack;
		setStack(ItemStack.EMPTY);
		return taken;
	}

	protected void tickDisplay(ServerLevel level) {
		if (--syncTimer <= 0) {
			syncTimer = SYNC_INTERVAL;
			syncDisplay(level);
		}
	}

	private AABB displayArea() {
		BlockPos pos = getBlockPos();
		return new AABB(pos.getX(), pos.getY() + 0.9, pos.getZ(), pos.getX() + 1, pos.getY() + 1.8, pos.getZ() + 1);
	}

	private void syncDisplay(ServerLevel level) {
		List<ItemEntity> displays = level.getEntitiesOfClass(ItemEntity.class, displayArea(), e -> e.isNoGravity() && e.hasPickUpDelay());
		boolean kept = false;
		for (ItemEntity display : displays) {
			if (!kept && !stack.isEmpty() && ItemStack.isSameItemSameComponents(display.getItem(), stack)) {
				kept = true;
			} else {
				display.discard();
			}
		}
		if (!kept && !stack.isEmpty()) {
			BlockPos pos = getBlockPos();
			ItemEntity display = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, stack.copyWithCount(1), 0, 0, 0);
			display.makeFakeItem();
			display.setNoGravity(true);
			display.setUnlimitedLifetime();
			level.addFreshEntity(display);
		}
	}

	private void removeDisplay(ServerLevel level) {
		level.getEntitiesOfClass(ItemEntity.class, displayArea(), e -> e.isNoGravity() && e.hasPickUpDelay()).forEach(Entity::discard);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		stack = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (!stack.isEmpty()) {
			output.store("Item", ItemStack.CODEC, stack);
		}
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level instanceof ServerLevel serverLevel) {
			removeDisplay(serverLevel);
			if (!stack.isEmpty()) {
				Containers.dropItemStack(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
				stack = ItemStack.EMPTY;
			}
		}
		super.preRemoveSideEffects(pos, state);
	}
}
