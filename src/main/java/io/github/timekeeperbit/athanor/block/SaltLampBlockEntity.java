package io.github.timekeeperbit.athanor.block;

import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import io.github.timekeeperbit.athanor.world.Aura;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Slowly draws miasma out of the chunk it stands in. */
public class SaltLampBlockEntity extends BlockEntity {
	public static final int INTERVAL = 100;
	public static final int PURIFY = 3;

	public SaltLampBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SALT_LAMP, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SaltLampBlockEntity lamp) {
		if (level.getGameTime() % INTERVAL != Math.floorMod(pos.asLong(), INTERVAL)) {
			return;
		}
		ServerLevel serverLevel = (ServerLevel) level;
		if (Aura.purify(serverLevel, pos, PURIFY) > 0) {
			serverLevel.sendParticles(ParticleTypes.WHITE_ASH, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 6, 0.3, 0.2, 0.3, 0.0);
		}
	}
}
