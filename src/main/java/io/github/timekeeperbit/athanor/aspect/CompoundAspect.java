package io.github.timekeeperbit.athanor.aspect;

import io.github.timekeeperbit.athanor.registry.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

/** Higher aspects, synthesized from two basic aspect crystals at a crafting table. Used as ritual ingredients. */
public enum CompoundAspect {
	LUX("lux", Aspect.IGNIS, Aspect.AER, 0xFFFFF27A),
	POTENTIA("potentia", Aspect.IGNIS, Aspect.ORDO, 0xFFF0A040),
	MOTUS("motus", Aspect.AER, Aspect.ORDO, 0xFFB0E0F0),
	PRAECANTATIO("praecantatio", Aspect.ORDO, Aspect.PERDITIO, 0xFFB050F0),
	ANIMA("anima", Aspect.VITA, Aspect.AER, 0xFFE0A0E0),
	INSTRUMENTUM("instrumentum", Aspect.METALLUM, Aspect.ORDO, 0xFF6080C0);

	private final String name;
	private final Aspect first;
	private final Aspect second;
	private final int color;

	CompoundAspect(String name, Aspect first, Aspect second, int color) {
		this.name = name;
		this.first = first;
		this.second = second;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public Aspect first() {
		return first;
	}

	public Aspect second() {
		return second;
	}

	public int getColor() {
		return color;
	}

	public MutableComponent displayName() {
		return Component.translatable("aspect.athanor." + name);
	}

	public Item crystal() {
		return ModItems.COMPOUND_CRYSTALS.get(this);
	}
}
