package io.github.timekeeperbit.athanor.client;

import io.github.timekeeperbit.athanor.menu.ResolverMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ResolverScreen extends AbstractContainerScreen<ResolverMenu> {
	public ResolverScreen(ResolverMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		Panels.panel(g, leftPos, topPos, imageWidth, imageHeight);
		Panels.slots(g, menu, leftPos, topPos);
		Panels.arrow(g, leftPos + 50, topPos + 35, menu.progress());
	}
}
