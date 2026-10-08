package io.github.timekeeperbit.athanor.item;

import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Transmutes the clicked block into a related block. Each transmutation costs one durability. */
public class PhilosophersStoneItem extends Item {
	public static final int DURABILITY = 256;
	private static Map<Block, Block> transmutations;

	public PhilosophersStoneItem(Properties properties) {
		super(properties);
	}

	public static Map<Block, Block> transmutations() {
		if (transmutations == null) {
			transmutations = Map.ofEntries(
					Map.entry(Blocks.COBBLESTONE, Blocks.STONE),
					Map.entry(Blocks.STONE, Blocks.STONE_BRICKS),
					Map.entry(Blocks.SAND, Blocks.GLASS),
					Map.entry(Blocks.DIRT, Blocks.GRASS_BLOCK),
					Map.entry(Blocks.COPPER_ORE, Blocks.IRON_ORE),
					Map.entry(Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_IRON_ORE),
					Map.entry(Blocks.IRON_ORE, Blocks.GOLD_ORE),
					Map.entry(Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_GOLD_ORE),
					Map.entry(Blocks.COAL_ORE, Blocks.LAPIS_ORE),
					Map.entry(Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_LAPIS_ORE),
					Map.entry(Blocks.NETHERRACK, Blocks.SOUL_SAND));
		}
		return transmutations;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		if (!(context.getLevel() instanceof ServerLevel level)) {
			return InteractionResult.SUCCESS;
		}
		BlockPos pos = context.getClickedPos();
		Block target = transmutations().get(level.getBlockState(pos).getBlock());
		if (target == null) {
			return InteractionResult.PASS;
		}
		level.setBlockAndUpdate(pos, target.defaultBlockState());
		level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 1.2F);
		level.sendParticles(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 8, 0.3, 0.2, 0.3, 0.0);
		if (context.getPlayer() != null) {
			context.getItemInHand().hurtAndBreak(1, context.getPlayer(), context.getHand());
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("tooltip.athanor.philosophers_stone").withStyle(ChatFormatting.GRAY));
	}
}
