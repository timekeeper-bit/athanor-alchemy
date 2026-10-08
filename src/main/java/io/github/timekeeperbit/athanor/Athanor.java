package io.github.timekeeperbit.athanor;

import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import io.github.timekeeperbit.athanor.registry.ModBlocks;
import io.github.timekeeperbit.athanor.registry.ModCreativeTab;
import io.github.timekeeperbit.athanor.registry.ModItems;
import io.github.timekeeperbit.athanor.registry.ModMenus;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Athanor implements ModInitializer {
	public static final String MOD_ID = "athanor";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlocks.init();
		ModItems.init();
		ModBlockEntities.init();
		ModMenus.init();
		ModCreativeTab.init();
		LOGGER.info("Athanor Alchemy initialized");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
