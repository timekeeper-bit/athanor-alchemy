package io.github.timekeeperbit.athanor.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

/** The in-game guide. It carries book content made of translatable pages and opens in the vanilla book screen. */
public class EmeraldTabletItem extends Item {
	public static final int PAGES = 12;

	public EmeraldTabletItem(Properties properties) {
		super(properties);
	}

	public static WrittenBookContent content() {
		List<Filterable<Component>> pages = new ArrayList<>();
		for (int i = 1; i <= PAGES; i++) {
			pages.add(Filterable.passThrough(Component.translatable("guide.athanor.page" + i)));
		}
		return new WrittenBookContent(Filterable.passThrough("Tabula Smaragdina"), "Hermes Trismegistus", 0, pages, true);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		player.openItemGui(player.getItemInHand(hand), hand);
		return InteractionResult.SUCCESS;
	}
}
