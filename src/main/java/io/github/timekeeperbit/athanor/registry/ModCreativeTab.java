package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.aspect.Aspect;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class ModCreativeTab {
	public static final CreativeModeTab MAIN = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Athanor.id("main"),
			FabricCreativeModeTab.builder()
					.title(Component.translatable("itemGroup.athanor.main"))
					.icon(() -> new ItemStack(ModBlocks.ATHANOR_CORE))
					.displayItems((parameters, output) -> {
						output.accept(ModBlocks.RESOLVER);
						output.accept(ModBlocks.ATHANOR_CORE);
						output.accept(ModBlocks.ATHANOR_BRICKS);
						output.accept(ModBlocks.ALCHEMICAL_GLASS);
						output.accept(ModItems.ORE_MAGNET);
						output.accept(ModItems.ITEM_MAGNET);
						for (Aspect aspect : Aspect.values()) {
							output.accept(aspect.crystal());
						}
					})
					.build());

	private ModCreativeTab() {
	}

	public static void init() {
	}
}
