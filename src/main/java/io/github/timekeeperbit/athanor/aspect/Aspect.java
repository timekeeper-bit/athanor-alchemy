package io.github.timekeeperbit.athanor.aspect;

import io.github.timekeeperbit.athanor.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

/** The eight basic aspects. The ordinal is used as an index everywhere (slots, pools, synced data). */
public enum Aspect {
	TERRA("terra", 0xFF7A5A32),
	AQUA("aqua", 0xFF3478DC),
	IGNIS("ignis", 0xFFE85420),
	AER("aer", 0xFFE6E696),
	ORDO("ordo", 0xFFECECFC),
	PERDITIO("perditio", 0xFF5A505A),
	VITA("vita", 0xFF40C848),
	METALLUM("metallum", 0xFFA8A8C4);

	/** Number of aspects; a literal so it can be used in switch labels. */
	public static final int COUNT = 8;

	private final String name;
	private final int color;

	Aspect(String name, int color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	/** ARGB colour used for bars and tooltips. */
	public int getColor() {
		return color;
	}

	public MutableComponent displayName() {
		return Component.translatable("aspect.athanor." + name);
	}

	public Item crystal() {
		return ModItems.CRYSTALS.get(this);
	}

	public static Aspect fromCrystal(Item item) {
		for (Aspect aspect : values()) {
			if (aspect.crystal() == item) {
				return aspect;
			}
		}
		return null;
	}
}
