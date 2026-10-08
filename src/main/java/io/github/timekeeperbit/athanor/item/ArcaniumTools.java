package io.github.timekeeperbit.athanor.item;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.registry.ModItems;
import io.github.timekeeperbit.athanor.world.Aura;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

/** Arcanium tools: between iron and diamond in power, highly enchantable, and they slowly mend by drawing aether. */
public final class ArcaniumTools {
	public static final TagKey<Item> REPAIR_ITEMS = TagKey.create(Registries.ITEM, Athanor.id("arcanium_tool_materials"));
	public static final ToolMaterial MATERIAL = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1200, 8.5F, 3.5F, 22, REPAIR_ITEMS);
	/** Durability restored per mending step, for one point of aether. */
	public static final int MEND = 4;

	private ArcaniumTools() {
	}

	public static boolean isArcanium(ItemStack stack) {
		return ModItems.ARCANIUM_TOOLS.contains(stack.getItem());
	}

	/** Mends every damaged Arcanium tool in the player's inventory. Returns how many were mended. */
	public static int selfRepair(ServerLevel level, Player player) {
		int mended = 0;
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (isArcanium(stack) && stack.isDamaged() && Aura.consume(level, player.blockPosition(), 1)) {
				stack.setDamageValue(Math.max(0, stack.getDamageValue() - MEND));
				mended++;
			}
		}
		return mended;
	}
}
