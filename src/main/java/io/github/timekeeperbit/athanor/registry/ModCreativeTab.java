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
						output.accept(ModItems.EMERALD_TABLET);
						output.accept(ModBlocks.ALEMBIC);
						output.accept(ModBlocks.HERMETIC_VESSEL);
						output.accept(ModBlocks.SALT_LAMP);
						output.accept(ModItems.ALCHEMIST_LENS);
						output.accept(ModItems.ALKAHEST);
						output.accept(ModItems.QUICKSILVER_MIRROR);
						output.accept(ModItems.OUROBOROS_RING);
						output.accept(ModItems.ARCANIUM_PICKAXE);
						output.accept(ModItems.ARCANIUM_AXE);
						output.accept(ModItems.ARCANIUM_SHOVEL);
						output.accept(ModItems.ARCANIUM_SWORD);
						output.accept(ModItems.ARCANIUM_HOE);
						output.accept(ModItems.AQUA_VITAE);
						output.accept(ModItems.PANACEA);
						output.accept(ModItems.ELIXIR_OF_LIFE);
						output.accept(ModItems.PRIMA_MATERIA);
						output.accept(ModItems.NIGREDO);
						output.accept(ModItems.ALBEDO);
						output.accept(ModItems.CITRINITAS);
						output.accept(ModItems.RUBEDO);
						output.accept(ModItems.SALT);
						output.accept(ModItems.SULFUR);
						output.accept(ModItems.QUICKSILVER);
						output.accept(ModItems.CINNABAR);
						output.accept(ModItems.RAW_SILVER);
						output.accept(ModItems.SILVER_INGOT);
						output.accept(ModItems.RAW_LEAD);
						output.accept(ModItems.LEAD_INGOT);
						output.accept(ModBlocks.CINNABAR_ORE);
						output.accept(ModBlocks.DEEPSLATE_CINNABAR_ORE);
						output.accept(ModBlocks.SILVER_ORE);
						output.accept(ModBlocks.DEEPSLATE_SILVER_ORE);
						output.accept(ModBlocks.LEAD_ORE);
						output.accept(ModBlocks.DEEPSLATE_LEAD_ORE);
						output.accept(ModBlocks.ROCK_SALT);
						output.accept(ModBlocks.SILVER_BLOCK);
						output.accept(ModBlocks.LEAD_BLOCK);
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
