package io.github.timekeeperbit.athanor.item;

import io.github.timekeeperbit.athanor.world.Aura;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/** Salt, the fixed principle. Scattering a pinch clears some miasma from the chunk. */
public class SaltItem extends Item {
	public static final int PURIFY = 15;

	public SaltItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}
		int removed = Aura.purify(serverLevel, player.blockPosition(), PURIFY);
		if (removed == 0) {
			player.sendOverlayMessage(Component.translatable("message.athanor.salt.clean"));
			return InteractionResult.PASS;
		}
		player.getItemInHand(hand).consume(1, player);
		serverLevel.playSound(null, player.blockPosition(), SoundEvents.SAND_BREAK, SoundSource.PLAYERS, 0.8F, 1.4F);
		serverLevel.sendParticles(ParticleTypes.WHITE_ASH, player.getX(), player.getY() + 1.0, player.getZ(), 30, 1.0, 0.5, 1.0, 0.0);
		player.sendOverlayMessage(Component.translatable("message.athanor.salt.purified", removed));
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		lines.accept(Component.translatable("tooltip.athanor.salt").withStyle(ChatFormatting.GRAY));
	}
}
