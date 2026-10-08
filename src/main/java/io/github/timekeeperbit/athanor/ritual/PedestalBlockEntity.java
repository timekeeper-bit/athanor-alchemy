package io.github.timekeeperbit.athanor.ritual;

import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PedestalBlockEntity extends ItemHolderBlockEntity {
	public PedestalBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.PEDESTAL, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, PedestalBlockEntity pedestal) {
		pedestal.tickDisplay((ServerLevel) level);
	}
}
