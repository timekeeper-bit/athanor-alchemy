package io.github.timekeeperbit.athanor.item;

import io.github.timekeeperbit.athanor.world.Aura;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/** Sneak-use to fix a place in the mirror; use to step back to it. Each journey spends aether where you stand. */
public class QuicksilverMirrorItem extends Item {
	public static final int AETHER_COST = 20;
	public static final int COOLDOWN = 100;

	public QuicksilverMirrorItem(Properties properties) {
		super(properties);
	}

	public static void bind(ItemStack stack, ServerLevel level, BlockPos pos) {
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
			tag.putInt("MirrorX", pos.getX());
			tag.putInt("MirrorY", pos.getY());
			tag.putInt("MirrorZ", pos.getZ());
			tag.putString("MirrorLevel", level.dimension().toString());
		});
	}

	/** The bound position, or null if the mirror is unbound or bound in another dimension. */
	public static BlockPos target(ItemStack stack, ServerLevel level) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null) {
			return null;
		}
		CompoundTag tag = data.copyTag();
		if (!tag.contains("MirrorX") || !tag.getStringOr("MirrorLevel", "").equals(level.dimension().toString())) {
			return null;
		}
		return new BlockPos(tag.getIntOr("MirrorX", 0), tag.getIntOr("MirrorY", 0), tag.getIntOr("MirrorZ", 0));
	}

	private static boolean isBound(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		return data != null && data.copyTag().contains("MirrorX");
	}

	/** Teleports the player to the mirror's place. Returns the message key describing the outcome. */
	public static String travel(ItemStack stack, ServerLevel level, Player player) {
		BlockPos target = target(stack, level);
		if (target == null) {
			return isBound(stack) ? "message.athanor.mirror.other_world" : "message.athanor.mirror.unbound";
		}
		BlockPos from = player.blockPosition();
		if (!Aura.consume(level, from, AETHER_COST)) {
			return "message.athanor.mirror.no_aether";
		}
		level.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.4, 0.8, 0.4, 0.05);
		player.teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
		level.playSound(null, target, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.8F, 1.2F);
		return "message.athanor.mirror.travelled";
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.SUCCESS;
		}
		ItemStack stack = player.getItemInHand(hand);
		if (player.isShiftKeyDown()) {
			bind(stack, serverLevel, player.blockPosition());
			player.sendOverlayMessage(Component.translatable("message.athanor.mirror.bound"));
			return InteractionResult.SUCCESS;
		}
		String result = travel(stack, serverLevel, player);
		player.sendOverlayMessage(Component.translatable(result));
		if (result.equals("message.athanor.mirror.travelled")) {
			player.getCooldowns().addCooldown(stack, COOLDOWN);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		lines.accept(Component.translatable("tooltip.athanor.quicksilver_mirror").withStyle(ChatFormatting.GRAY));
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data != null && data.copyTag().contains("MirrorX")) {
			CompoundTag tag = data.copyTag();
			lines.accept(Component.translatable("tooltip.athanor.quicksilver_mirror.bound",
					tag.getIntOr("MirrorX", 0), tag.getIntOr("MirrorY", 0), tag.getIntOr("MirrorZ", 0)).withStyle(ChatFormatting.DARK_AQUA));
		}
	}
}
