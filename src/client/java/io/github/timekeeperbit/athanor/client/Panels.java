package io.github.timekeeperbit.athanor.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

/** Draws vanilla-style panels and slots with plain fills, so the mod needs no GUI textures. */
final class Panels {
	static final int BACKGROUND = 0xFFC6C6C6;
	static final int LIGHT = 0xFFFFFFFF;
	static final int DARK = 0xFF555555;
	static final int SLOT = 0xFF8B8B8B;
	static final int SLOT_DARK = 0xFF373737;
	static final int TEXT = 0xFF404040;

	private Panels() {
	}

	static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
		g.fill(x + 1, y, x + w - 1, y + h, 0xFF000000);
		g.fill(x, y + 1, x + w, y + h - 1, 0xFF000000);
		g.fill(x + 1, y + 1, x + w - 1, y + h - 1, BACKGROUND);
		g.fill(x + 1, y + 1, x + w - 2, y + 3, LIGHT);
		g.fill(x + 1, y + 1, x + 3, y + h - 2, LIGHT);
		g.fill(x + 3, y + h - 3, x + w - 1, y + h - 1, DARK);
		g.fill(x + w - 3, y + 3, x + w - 1, y + h - 1, DARK);
	}

	/** A sunken box; (x, y) is the inner top-left corner. */
	static void inset(GuiGraphicsExtractor g, int x, int y, int w, int h) {
		g.fill(x - 1, y - 1, x + w + 1, y + h + 1, LIGHT);
		g.fill(x - 1, y - 1, x + w, y + h, SLOT_DARK);
		g.fill(x, y, x + w, y + h, SLOT);
	}

	static void slots(GuiGraphicsExtractor g, AbstractContainerMenu menu, int left, int top) {
		for (Slot slot : menu.slots) {
			inset(g, left + slot.x, top + slot.y, 16, 16);
		}
	}

	/** Right-pointing progress arrow, 22x15. */
	static void arrow(GuiGraphicsExtractor g, int x, int y, float progress) {
		g.fill(x, y + 5, x + 16, y + 10, DARK);
		for (int i = 0; i < 7; i++) {
			g.fill(x + 15 + i, y + i, x + 16 + i, y + 15 - i, DARK);
		}
		int filled = Math.round(22 * progress);
		if (filled > 0) {
			g.fill(x, y + 5, x + Math.min(filled, 16), y + 10, 0xFFFFFFFF);
			for (int i = 0; i < 7 && 16 + i < filled; i++) {
				g.fill(x + 15 + i, y + i, x + 16 + i, y + 15 - i, 0xFFFFFFFF);
			}
		}
	}
}
