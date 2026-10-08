package io.github.timekeeperbit.athanor.client;

import io.github.timekeeperbit.athanor.menu.AlembicMenu;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AlembicScreen extends AbstractContainerScreen<AlembicMenu> {
	private static final int ARROW_X = 74;
	private static final int ARROW_Y = 35;

	public AlembicScreen(AlembicMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		Panels.panel(g, leftPos, topPos, imageWidth, imageHeight);
		Panels.slots(g, menu, leftPos, topPos);
		Panels.arrow(g, leftPos + ARROW_X, topPos + ARROW_Y, menu.progress());
		Panels.flame(g, leftPos + AlembicMenu.HEAT_X + 2, topPos + AlembicMenu.HEAT_Y + 2, menu.isHeated());
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		super.extractTooltip(g, mouseX, mouseY);
		if (isHovering(AlembicMenu.HEAT_X, AlembicMenu.HEAT_Y, 16, 16, mouseX, mouseY)) {
			Component line = Component.translatable(menu.isHeated() ? "gui.athanor.heated" : "gui.athanor.no_heat")
					.withStyle(menu.isHeated() ? ChatFormatting.GOLD : ChatFormatting.RED);
			g.setComponentTooltipForNextFrame(font, List.of(line), mouseX, mouseY);
		}
	}
}
