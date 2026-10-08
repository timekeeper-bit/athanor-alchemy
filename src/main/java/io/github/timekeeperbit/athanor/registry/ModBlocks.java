package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlock;
import io.github.timekeeperbit.athanor.block.ResolverBlock;
import io.github.timekeeperbit.athanor.ritual.PedestalBlock;
import io.github.timekeeperbit.athanor.ritual.RitualAltarBlock;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public final class ModBlocks {
	public static final Block ATHANOR_BRICKS = register("athanor_bricks", Block::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F, 6.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops());
	public static final Block ALCHEMICAL_GLASS = register("alchemical_glass", TransparentBlock::new,
			BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.GLASS).noOcclusion()
					.isRedstoneConductor((state, level, pos) -> false)
					.isSuffocating((state, level, pos) -> false));
	public static final Block RESOLVER = register("resolver", ResolverBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F).sound(SoundType.METAL).requiresCorrectToolForDrops());
	public static final Block ATHANOR_CORE = register("athanor_core", AthanorCoreBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5F, 6.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops()
					.lightLevel(state -> state.getValue(AthanorCoreBlock.FORMED) ? 13 : 0));
	public static final Block RITUAL_ALTAR = register("ritual_altar", RitualAltarBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(3.5F, 6.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops()
					.lightLevel(state -> 7));
	public static final Block PEDESTAL = register("arcane_pedestal", PedestalBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).strength(2.0F, 6.0F).sound(SoundType.DEEPSLATE_BRICKS).requiresCorrectToolForDrops().noOcclusion());

	private ModBlocks() {
	}

	private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, BlockBehaviour.Properties properties) {
		ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, Athanor.id(name));
		Block block = Registry.register(BuiltInRegistries.BLOCK, blockKey, factory.apply(properties.setId(blockKey)));
		ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Athanor.id(name));
		Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, new Item.Properties().setId(itemKey).useBlockDescriptionPrefix()));
		return block;
	}

	public static void init() {
	}
}
