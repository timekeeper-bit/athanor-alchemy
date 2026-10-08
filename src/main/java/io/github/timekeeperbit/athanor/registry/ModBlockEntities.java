package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlockEntity;
import io.github.timekeeperbit.athanor.block.ResolverBlockEntity;
import io.github.timekeeperbit.athanor.ritual.PedestalBlockEntity;
import io.github.timekeeperbit.athanor.ritual.RitualAltarBlockEntity;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<ResolverBlockEntity> RESOLVER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Athanor.id("resolver"), new BlockEntityType<>(ResolverBlockEntity::new, Set.of(ModBlocks.RESOLVER)));
	public static final BlockEntityType<AthanorCoreBlockEntity> ATHANOR_CORE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Athanor.id("athanor_core"), new BlockEntityType<>(AthanorCoreBlockEntity::new, Set.of(ModBlocks.ATHANOR_CORE)));
	public static final BlockEntityType<RitualAltarBlockEntity> RITUAL_ALTAR = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Athanor.id("ritual_altar"), new BlockEntityType<>(RitualAltarBlockEntity::new, Set.of(ModBlocks.RITUAL_ALTAR)));
	public static final BlockEntityType<PedestalBlockEntity> PEDESTAL = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Athanor.id("arcane_pedestal"), new BlockEntityType<>(PedestalBlockEntity::new, Set.of(ModBlocks.PEDESTAL)));

	private ModBlockEntities() {
	}

	public static void init() {
	}
}
