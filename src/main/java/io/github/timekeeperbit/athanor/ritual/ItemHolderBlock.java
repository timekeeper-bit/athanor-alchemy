package io.github.timekeeperbit.athanor.ritual;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Right-click with an item to place one on the block; right-click with an empty hand to take it back. */
public abstract class ItemHolderBlock extends BaseEntityBlock {
	protected ItemHolderBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof ItemHolderBlockEntity holder) || !holder.getStack().isEmpty()) {
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		}
		if (!level.isClientSide()) {
			holder.setStack(stack.copyWithCount(1));
			if (!player.isCreative()) {
				stack.shrink(1);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof ItemHolderBlockEntity holder) || holder.getStack().isEmpty()) {
			return InteractionResult.PASS;
		}
		if (holder instanceof RitualAltarBlockEntity altar && altar.isActive()) {
			return InteractionResult.FAIL;
		}
		if (!level.isClientSide()) {
			ItemStack taken = holder.takeStack();
			player.getInventory().add(taken);
			if (!taken.isEmpty()) {
				Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), taken);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
