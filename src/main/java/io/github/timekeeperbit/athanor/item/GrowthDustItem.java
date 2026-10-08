package io.github.timekeeperbit.athanor.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** Applies three rounds of bone meal to every block in a 5x5 area around the clicked block. */
public class GrowthDustItem extends Item {
	public static final int RADIUS = 2;
	public static final int ROUNDS = 3;

	public GrowthDustItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			int grown = grow(serverLevel, context.getClickedPos());
			if (grown == 0) {
				return InteractionResult.PASS;
			}
			if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
				context.getItemInHand().shrink(1);
			}
		}
		return InteractionResult.SUCCESS;
	}

	/** Returns how many bone meal applications succeeded. */
	public static int grow(ServerLevel level, BlockPos center) {
		int grown = 0;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -1, -RADIUS), center.offset(RADIUS, 1, RADIUS))) {
			BlockPos at = pos.immutable();
			for (int i = 0; i < ROUNDS; i++) {
				if (BoneMealItem.growCrop(new ItemStack(Items.BONE_MEAL), level, at)) {
					grown++;
				}
			}
		}
		if (grown > 0) {
			level.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.getX() + 0.5, center.getY() + 1, center.getZ() + 0.5, 30, 2, 0.5, 2, 0);
		}
		return grown;
	}
}
