package io.github.timekeeperbit.athanor.item;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.AspectList;
import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.world.Aura;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** The universal solvent: dissolves a block in place into its aspect crystals. Leaves a little miasma. */
public class AlkahestItem extends Item {
	public static final int DURABILITY = 64;

	public AlkahestItem(Properties properties) {
		super(properties);
	}

	/** Dissolves the block at {@code pos}. Returns false if it cannot be dissolved. */
	public static boolean dissolve(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.isAir() || state.hasBlockEntity() || state.getDestroySpeed(level, pos) < 0) {
			return false;
		}
		AspectList aspects = AspectTable.get(new ItemStack(state.getBlock()));
		if (aspects.isEmpty()) {
			return false;
		}
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		for (Aspect aspect : Aspect.values()) {
			int amount = aspects.get(aspect);
			if (amount > 0) {
				Block.popResource(level, pos, new ItemStack(aspect.crystal(), amount));
			}
		}
		Aura.addMiasma(level, pos, 1);
		level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1.6F);
		level.sendParticles(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 12, 0.3, 0.3, 0.3, 0.0);
		return true;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		Player player = context.getPlayer();
		if (!dissolve(level, context.getClickedPos())) {
			if (player != null) {
				player.sendOverlayMessage(Component.translatable("message.athanor.alkahest.resists"));
			}
			return InteractionResult.FAIL;
		}
		if (player != null) {
			context.getItemInHand().hurtAndBreak(1, player, context.getHand());
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		lines.accept(Component.translatable("tooltip.athanor.alkahest").withStyle(ChatFormatting.GRAY));
	}
}
