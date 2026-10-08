package io.github.timekeeperbit.athanor.client;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlockEntity;
import io.github.timekeeperbit.athanor.menu.AthanorMenu;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AthanorScreen extends AbstractContainerScreen<AthanorMenu> {
	private static final int BAR_X = 8;
	private static final int BAR_Y = 18;
	private static final int BAR_W = 8;
	private static final int BAR_STEP = 11;
	private static final int BAR_H = 60;
	private static final int ARROW_X = 127;
	private static final int ARROW_Y = 54;

	public AthanorScreen(AthanorMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 184);
		inventoryLabelY = AthanorMenu.INVENTORY_Y - 12;
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
		super.extractBackground(g, mouseX, mouseY, partialTick);
		int x = leftPos;
		int y = topPos;
		Panels.panel(g, x, y, imageWidth, imageHeight);
		Panels.slots(g, menu, x, y);

		AthanorRecipe recipe = menu.currentRecipe();
		for (Aspect aspect : Aspect.values()) {
			int bx = x + BAR_X + aspect.ordinal() * BAR_STEP;
			int by = y + BAR_Y;
			int amount = menu.pool(aspect);
			int need = recipe == null ? 0 : recipe.cost().get(aspect);
			if (need > amount) {
				g.fill(bx - 2, by - 2, bx + BAR_W + 2, by + BAR_H + 2, 0xFFD02020);
			}
			Panels.inset(g, bx, by, BAR_W, BAR_H);
			g.fill(bx, by, bx + BAR_W, by + BAR_H, 0xFF2A2030);
			int filled = scale(amount);
			if (filled > 0) {
				g.fill(bx, by + BAR_H - filled, bx + BAR_W, by + BAR_H, aspect.getColor());
			}
			if (need > 0) {
				int line = by + BAR_H - Math.max(1, scale(need));
				g.fill(bx - 1, line, bx + BAR_W + 1, line + 1, 0xFFFFFFFF);
			}
		}

		if (menu.hasCatalyst() && recipe == null) {
			int sx = x + AthanorMenu.CATALYST_X;
			int sy = y + AthanorMenu.CATALYST_Y;
			g.outline(sx - 2, sy - 2, 20, 20, 0xFFD02020);
		}
		if (recipe != null) {
			// Ghost of the result next to the arrow.
			g.fakeItem(recipe.resultStack(), x + AthanorMenu.OUTPUT_X, y + AthanorMenu.OUTPUT_Y - 24);
		}
		Panels.arrow(g, x + ARROW_X, y + ARROW_Y, menu.progress());

		Component status = Component.translatable(menu.isFormed() ? "gui.athanor.formed" : "gui.athanor.not_formed");
		int color = menu.isFormed() ? 0xFF207020 : 0xFFB02020;
		g.text(font, status, x + imageWidth - 8 - font.width(status), y + 6, color, false);
	}

	private static int scale(int amount) {
		return Math.min(BAR_H, Math.round((float) amount * BAR_H / AthanorCoreBlockEntity.POOL_CAPACITY));
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor g, int mouseX, int mouseY) {
		super.extractTooltip(g, mouseX, mouseY);
		AthanorRecipe recipe = menu.currentRecipe();
		for (Aspect aspect : Aspect.values()) {
			if (isHovering(BAR_X + aspect.ordinal() * BAR_STEP - 1, BAR_Y - 1, BAR_W + 2, BAR_H + 2, mouseX, mouseY)) {
				List<Component> lines = new ArrayList<>();
				lines.add(Component.translatable("gui.athanor.pool", aspect.displayName(), menu.pool(aspect), AthanorCoreBlockEntity.POOL_CAPACITY)
						.withStyle(style -> style.withColor(aspect.getColor() & 0xFFFFFF)));
				int need = recipe == null ? 0 : recipe.cost().get(aspect);
				if (need > 0) {
					lines.add(Component.translatable("gui.athanor.need", need)
							.withStyle(need > menu.pool(aspect) ? ChatFormatting.RED : ChatFormatting.GRAY));
				}
				g.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
				return;
			}
		}
		if (isHovering(ARROW_X, ARROW_Y, 22, 15, mouseX, mouseY)) {
			List<Component> lines = new ArrayList<>();
			lines.add(Component.translatable(menu.isFormed() ? "gui.athanor.formed" : "gui.athanor.not_formed"));
			if (menu.hasCatalyst() && recipe == null) {
				lines.add(Component.translatable("gui.athanor.no_recipe").withStyle(ChatFormatting.RED));
			} else if (recipe != null) {
				lines.add(recipe.resultStack().getHoverName().copy().append(" ×" + recipe.count()));
				lines.add(recipe.cost().describe());
			}
			g.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
		}
	}
}
