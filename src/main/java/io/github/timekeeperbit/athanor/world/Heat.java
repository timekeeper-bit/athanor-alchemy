package io.github.timekeeperbit.athanor.world;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Heat sources that alchemical apparatus can stand on. */
public final class Heat {
	private Heat() {
	}

	public static boolean isHeat(BlockState state) {
		if (state.is(Blocks.MAGMA_BLOCK) || state.is(Blocks.LAVA) || state.is(BlockTags.FIRE)) {
			return true;
		}
		return state.is(BlockTags.CAMPFIRES) && state.getValue(CampfireBlock.LIT);
	}

	public static boolean below(Level level, BlockPos pos) {
		return isHeat(level.getBlockState(pos.below()));
	}
}
