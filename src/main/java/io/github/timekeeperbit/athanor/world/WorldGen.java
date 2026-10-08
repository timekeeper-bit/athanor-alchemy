package io.github.timekeeperbit.athanor.world;

import io.github.timekeeperbit.athanor.Athanor;
import java.util.List;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Adds the mod's ores to every overworld biome. The features themselves are data in worldgen/. */
public final class WorldGen {
	public static final List<ResourceKey<PlacedFeature>> ORES = List.of(
			key("ore_cinnabar"), key("ore_silver"), key("ore_lead"), key("ore_rock_salt"));

	private WorldGen() {
	}

	private static ResourceKey<PlacedFeature> key(String name) {
		return ResourceKey.create(Registries.PLACED_FEATURE, Athanor.id(name));
	}

	public static void init() {
		for (ResourceKey<PlacedFeature> ore : ORES) {
			BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Decoration.UNDERGROUND_ORES, ore);
		}
	}
}
