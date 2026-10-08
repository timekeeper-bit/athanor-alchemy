package io.github.timekeeperbit.athanor.item;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

/** A drinkable alchemical preparation. Its effect is applied in code when drinking finishes. */
public class DrinkItem extends Item {
	private final BiConsumer<ServerLevel, LivingEntity> effect;
	private final String tooltip;

	public DrinkItem(Properties properties, BiConsumer<ServerLevel, LivingEntity> effect, String tooltip) {
		super(properties);
		this.effect = effect;
		this.tooltip = tooltip;
	}

	public void apply(ServerLevel level, LivingEntity entity) {
		effect.accept(level, entity);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		if (level instanceof ServerLevel serverLevel) {
			apply(serverLevel, entity);
		}
		return super.finishUsingItem(stack, level, entity);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		lines.accept(Component.translatable(tooltip).withStyle(ChatFormatting.GRAY));
	}
}
