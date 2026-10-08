package io.github.timekeeperbit.athanor.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** The serpent that eats its tail: while carried, it mends damaged items by consuming the bearer's experience. */
public class OuroborosRingItem extends Item {
	public static final int INTERVAL = 60;

	public OuroborosRingItem(Properties properties) {
		super(properties);
	}

	/** Mends one point on each damaged item, paying one experience point per point. Returns points mended. */
	public static int mend(Player player) {
		int mended = 0;
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (!stack.isDamaged() || stack.getItem() instanceof OuroborosRingItem) {
				continue;
			}
			if (!player.isCreative() && player.totalExperience <= 0) {
				break;
			}
			stack.setDamageValue(stack.getDamageValue() - 1);
			if (!player.isCreative()) {
				player.giveExperiencePoints(-1);
			}
			mended++;
		}
		return mended;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (entity instanceof Player player && level.getGameTime() % INTERVAL == 0) {
			mend(player);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		lines.accept(Component.translatable("tooltip.athanor.ouroboros_ring").withStyle(ChatFormatting.GRAY));
	}
}
