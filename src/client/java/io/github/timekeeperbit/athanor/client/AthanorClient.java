package io.github.timekeeperbit.athanor.client;

import io.github.timekeeperbit.athanor.aspect.AspectList;
import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;

public class AthanorClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenus.RESOLVER, ResolverScreen::new);
		MenuScreens.register(ModMenus.ATHANOR, AthanorScreen::new);
		MenuScreens.register(ModMenus.ALEMBIC, AlembicScreen::new);

		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			AspectList aspects = AspectTable.get(stack);
			if (!aspects.isEmpty()) {
				lines.add(Component.translatable("tooltip.athanor.aspects").withStyle(ChatFormatting.DARK_PURPLE)
						.append(" ").append(aspects.describe()));
			}
		});
	}
}
