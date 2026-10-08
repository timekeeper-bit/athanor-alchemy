package io.github.timekeeperbit.athanor.item;

import io.github.timekeeperbit.athanor.aspect.AspectList;
import io.github.timekeeperbit.athanor.aspect.AspectTable;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

/** Starts rituals (handled by the altar) and reveals the aspects of any block it touches. */
public class WandItem extends Item {
	public WandItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (!context.getLevel().isClientSide()) {
			BlockState state = context.getLevel().getBlockState(context.getClickedPos());
			AspectList aspects = AspectTable.get(new ItemStack(state.getBlock()));
			player.sendSystemMessage(aspects.isEmpty()
					? Component.translatable("message.athanor.wand.no_aspects", state.getBlock().getName())
					: Component.translatable("message.athanor.wand.aspects", state.getBlock().getName()).append(" ").append(aspects.describe()));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("tooltip.athanor.wand").withStyle(ChatFormatting.GRAY));
	}
}
