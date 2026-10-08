package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlockEntity;
import io.github.timekeeperbit.athanor.block.ResolverBlockEntity;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModBlockEntities {
	public static final BlockEntityType<ResolverBlockEntity> RESOLVER = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Athanor.id("resolver"), new BlockEntityType<>(ResolverBlockEntity::new, Set.of(ModBlocks.RESOLVER)));
	public static final BlockEntityType<AthanorCoreBlockEntity> ATHANOR_CORE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
			Athanor.id("athanor_core"), new BlockEntityType<>(AthanorCoreBlockEntity::new, Set.of(ModBlocks.ATHANOR_CORE)));

	private ModBlockEntities() {
	}

	public static void init() {
	}
}
