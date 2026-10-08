package io.github.timekeeperbit.athanor;

import io.github.timekeeperbit.athanor.registry.ModAttachments;
import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import io.github.timekeeperbit.athanor.registry.ModBlocks;
import io.github.timekeeperbit.athanor.registry.ModCreativeTab;
import io.github.timekeeperbit.athanor.registry.ModEffects;
import io.github.timekeeperbit.athanor.registry.ModItems;
import io.github.timekeeperbit.athanor.registry.ModMenus;
import io.github.timekeeperbit.athanor.world.WorldGen;
import io.github.timekeeperbit.athanor.world.WorldRules;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Athanor implements ModInitializer {
	public static final String MOD_ID = "athanor";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModAttachments.init();
		ModEffects.init();
		ModBlocks.init();
		ModItems.init();
		ModBlockEntities.init();
		ModMenus.init();
		ModCreativeTab.init();
		WorldGen.init();
		WorldRules.init();
		LOGGER.info("Athanor Alchemy initialized");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
