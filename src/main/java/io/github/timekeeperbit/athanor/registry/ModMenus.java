package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.menu.AthanorMenu;
import io.github.timekeeperbit.athanor.menu.ResolverMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
	public static final MenuType<ResolverMenu> RESOLVER = Registry.register(BuiltInRegistries.MENU, Athanor.id("resolver"),
			new MenuType<>(ResolverMenu::new, FeatureFlags.VANILLA_SET));
	public static final MenuType<AthanorMenu> ATHANOR = Registry.register(BuiltInRegistries.MENU, Athanor.id("athanor"),
			new MenuType<>(AthanorMenu::new, FeatureFlags.VANILLA_SET));

	private ModMenus() {
	}

	public static void init() {
	}
}
