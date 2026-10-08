package io.github.timekeeperbit.athanor.item;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

/** Grants a status effect while carried anywhere in the player's inventory. */
public class CharmItem extends Item {
	private static final int REFRESH_INTERVAL = 40;
	/** Longer than the refresh interval, and above 200 so night vision does not flicker. */
	private static final int DURATION = 260;

	private final Holder<MobEffect> effect;
	private final int amplifier;
	private final boolean onlyWhenHurt;

	public CharmItem(Properties properties, Holder<MobEffect> effect, int amplifier, boolean onlyWhenHurt) {
		super(properties);
		this.effect = effect;
		this.amplifier = amplifier;
		this.onlyWhenHurt = onlyWhenHurt;
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		if (!(entity instanceof Player player) || level.getGameTime() % REFRESH_INTERVAL != 0) {
			return;
		}
		if (onlyWhenHurt && player.getHealth() >= player.getMaxHealth()) {
			return;
		}
		player.addEffect(new MobEffectInstance(effect, DURATION, amplifier, true, false, true));
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("tooltip.athanor.charm", effect.value().getDisplayName()).withStyle(ChatFormatting.GRAY));
	}
}
