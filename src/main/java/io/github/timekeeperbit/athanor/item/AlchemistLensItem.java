package io.github.timekeeperbit.athanor.item;

import io.github.timekeeperbit.athanor.aspect.AspectList;
import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.opus.HermeticVesselBlockEntity;
import io.github.timekeeperbit.athanor.world.Aura;
import io.github.timekeeperbit.athanor.world.AuraState;
import io.github.timekeeperbit.athanor.world.Celestial;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Reads the aura of the chunk, the sky, the aspects of a block, or the progress of a Hermetic Vessel. */
public class AlchemistLensItem extends Item {
	public AlchemistLensItem(Properties properties) {
		super(properties);
	}

	public static MutableComponent describeAura(ServerLevel level, BlockPos pos) {
		AuraState aura = Aura.get(level, pos);
		ChatFormatting miasmaColor = aura.miasma() >= Aura.STRONG ? ChatFormatting.DARK_RED
				: aura.miasma() >= Aura.MILD ? ChatFormatting.GOLD : ChatFormatting.GREEN;
		MutableComponent line = Component.translatable("message.athanor.lens.aether", aura.aether(), Aura.base(pos)).withStyle(ChatFormatting.AQUA)
				.append(Component.literal(" · ").withStyle(ChatFormatting.GRAY))
				.append(Component.translatable("message.athanor.lens.miasma", aura.miasma()).withStyle(miasmaColor));
		if (Aura.isNode(pos)) {
			line.append(Component.literal(" · ").withStyle(ChatFormatting.GRAY))
					.append(Component.translatable("message.athanor.lens.node").withStyle(ChatFormatting.LIGHT_PURPLE));
		}
		line.append(Component.literal(" · ").withStyle(ChatFormatting.GRAY))
				.append((Celestial.isDay(level) ? Celestial.Time.DAY : Celestial.Time.NIGHT).describe().copy().withStyle(ChatFormatting.YELLOW));
		if (Celestial.isFullMoonNight(level, pos)) {
			line.append(Component.literal(" · ").withStyle(ChatFormatting.GRAY))
					.append(Component.translatable("celestial.athanor.full_moon").withStyle(ChatFormatting.WHITE));
		}
		return line;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level instanceof ServerLevel serverLevel) {
			player.sendSystemMessage(describeAura(serverLevel, player.blockPosition()));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Player player = context.getPlayer();
		if (player == null) {
			return InteractionResult.PASS;
		}
		if (context.getLevel() instanceof ServerLevel level) {
			BlockPos pos = context.getClickedPos();
			if (level.getBlockEntity(pos) instanceof HermeticVesselBlockEntity vessel) {
				player.sendSystemMessage(vessel.describe(level));
			} else {
				BlockState state = level.getBlockState(pos);
				AspectList aspects = AspectTable.get(new ItemStack(state.getBlock()));
				player.sendSystemMessage(aspects.isEmpty()
						? Component.translatable("message.athanor.wand.no_aspects", state.getBlock().getName())
						: Component.translatable("message.athanor.wand.aspects", state.getBlock().getName()).append(" ").append(aspects.describe()));
				player.sendSystemMessage(describeAura(level, pos));
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
		lines.accept(Component.translatable("tooltip.athanor.alchemist_lens").withStyle(ChatFormatting.GRAY));
	}
}
