package io.github.timekeeperbit.athanor.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** While switched on and anywhere in the inventory, pulls nearby item drops and experience to the player. */
public class ItemMagnetItem extends Item {
	public static final double RANGE = 8.0;

	public ItemMagnetItem(Properties properties) {
		super(properties);
	}

	/** The on/off state is stored as the enchantment glint, so it is visible at a glance. */
	public static boolean isOn(ItemStack stack) {
		return Boolean.TRUE.equals(stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE));
	}

	public static void setOn(ItemStack stack, boolean on) {
		stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, on);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide()) {
			boolean on = !isOn(stack);
			setOn(stack, on);
			player.sendSystemMessage(Component.translatable(on ? "message.athanor.item_magnet.on" : "message.athanor.item_magnet.off"));
			level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.8F, on ? 1.4F : 0.8F);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (!isOn(stack) || !(entity instanceof Player player) || player.isSpectator() || level.getGameTime() % 4 != 0) {
			return;
		}
		pull(level, player);
	}

	/** Moves every item without pickup delay and every experience orb in range onto the player. */
	public static int pull(ServerLevel level, Player player) {
		AABB area = player.getBoundingBox().inflate(RANGE);
		int moved = 0;
		for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area, e -> e.isAlive() && !e.hasPickUpDelay())) {
			item.setPos(player.getX(), player.getY(), player.getZ());
			moved++;
		}
		for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, area, Entity::isAlive)) {
			orb.setPos(player.getX(), player.getY(), player.getZ());
			moved++;
		}
		return moved;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("tooltip.athanor.item_magnet").withStyle(ChatFormatting.GRAY));
		tooltip.accept(isOn(stack)
				? Component.translatable("tooltip.athanor.item_magnet.on").withStyle(ChatFormatting.GREEN)
				: Component.translatable("tooltip.athanor.item_magnet.off").withStyle(ChatFormatting.RED));
	}
}
