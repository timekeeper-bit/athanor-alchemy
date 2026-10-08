package io.github.timekeeperbit.athanor.item;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Use: mines every ore within reach into the inventory. Sneak + use: counts ores in a larger radius. */
public class OreMagnetItem extends Item {
	public static final int DURABILITY = 384;
	public static final int PULL_RADIUS = 5;
	public static final int MAX_PER_USE = 32;
	public static final int SCAN_RADIUS = 16;
	public static final int COOLDOWN = 40;

	public OreMagnetItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (level instanceof ServerLevel serverLevel) {
			if (player.isShiftKeyDown()) {
				scan(serverLevel, player.blockPosition(), SCAN_RADIUS).forEach(player::sendSystemMessage);
			} else {
				int mined = pullOres(serverLevel, player.blockPosition(), PULL_RADIUS, player);
				if (mined > 0) {
					stack.hurtAndBreak(mined, player, hand);
					level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8F, 0.6F);
					player.sendSystemMessage(Component.translatable("message.athanor.ore_magnet.mined", mined));
				} else {
					player.sendSystemMessage(Component.translatable("message.athanor.ore_magnet.none"));
				}
			}
			player.getCooldowns().addCooldown(stack, COOLDOWN);
		}
		return InteractionResult.SUCCESS;
	}

	public static boolean isOre(BlockState state) {
		return state.is(ConventionalBlockTags.ORES);
	}

	/**
	 * Breaks up to {@link #MAX_PER_USE} ores around the centre as if mined with a diamond pickaxe and gives the drops to
	 * the player. Returns the number of blocks mined.
	 */
	public static int pullOres(ServerLevel level, BlockPos center, int radius, Player player) {
		ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
		int mined = 0;
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
			if (mined >= MAX_PER_USE) {
				break;
			}
			BlockState state = level.getBlockState(pos);
			if (!isOre(state)) {
				continue;
			}
			BlockPos at = pos.immutable();
			List<ItemStack> drops = Block.getDrops(state, level, at, level.getBlockEntity(at), player, tool);
			state.spawnAfterBreak(level, at, tool, true);
			level.destroyBlock(at, false, player, 512);
			for (ItemStack drop : drops) {
				player.getInventory().add(drop);
				if (!drop.isEmpty()) {
					Containers.dropItemStack(level, player.getX(), player.getY(), player.getZ(), drop);
				}
			}
			mined++;
		}
		return mined;
	}

	/** Chat lines listing each ore type found around the centre. */
	public static List<Component> scan(ServerLevel level, BlockPos center, int radius) {
		Map<Block, Integer> counts = new LinkedHashMap<>();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
			BlockState state = level.getBlockState(pos);
			if (isOre(state)) {
				counts.merge(state.getBlock(), 1, Integer::sum);
			}
		}
		if (counts.isEmpty()) {
			return List.of(Component.translatable("message.athanor.ore_magnet.none"));
		}
		List<Component> lines = new java.util.ArrayList<>();
		lines.add(Component.translatable("message.athanor.ore_magnet.scan", radius).withStyle(ChatFormatting.GOLD));
		counts.entrySet().stream()
				.sorted(Map.Entry.<Block, Integer>comparingByValue().reversed())
				.forEach(e -> lines.add(Component.translatable("message.athanor.ore_magnet.scan_entry", e.getKey().getName(), e.getValue())));
		return lines;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		tooltip.accept(Component.translatable("tooltip.athanor.ore_magnet", PULL_RADIUS, SCAN_RADIUS).withStyle(ChatFormatting.GRAY));
	}
}
