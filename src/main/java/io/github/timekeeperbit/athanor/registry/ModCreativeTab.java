package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.CompoundAspect;
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
						output.accept(ModBlocks.RITUAL_ALTAR);
						output.accept(ModBlocks.PEDESTAL);
						output.accept(ModItems.WAND);
						output.accept(ModItems.ARCANIUM_INGOT);
						output.accept(ModItems.PHILOSOPHERS_STONE);
						output.accept(ModItems.GROWTH_DUST);
						output.accept(ModItems.CHARM_SWIFTNESS);
						output.accept(ModItems.CHARM_VITALITY);
						output.accept(ModItems.CHARM_NIGHT_VISION);
						output.accept(ModItems.CHARM_TIDES);
						output.accept(ModItems.CHARM_EMBERS);
						output.accept(ModItems.ORE_MAGNET);
						output.accept(ModItems.ITEM_MAGNET);
						for (Aspect aspect : Aspect.values()) {
							output.accept(aspect.crystal());
						}
						for (CompoundAspect aspect : CompoundAspect.values()) {
							output.accept(aspect.crystal());
						}
					})
					.build());

	private ModCreativeTab() {
	}

	public static void init() {
	}
}
