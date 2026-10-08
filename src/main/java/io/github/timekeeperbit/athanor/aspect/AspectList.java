package io.github.timekeeperbit.athanor.aspect;

import java.util.Arrays;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/** An immutable amount of each aspect. */
public final class AspectList {
	public static final AspectList EMPTY = new AspectList(new int[Aspect.COUNT]);

	private final int[] amounts;

	private AspectList(int[] amounts) {
		this.amounts = amounts;
	}

	public static Builder builder() {
		return new Builder();
	}

	public int get(Aspect aspect) {
		return amounts[aspect.ordinal()];
	}

	public int total() {
		return Arrays.stream(amounts).sum();
	}

	public boolean isEmpty() {
		return total() == 0;
	}

	/** One line such as "Terra 2  Perditio 1", in aspect order. */
	public MutableComponent describe() {
		MutableComponent line = Component.empty();
		boolean first = true;
		for (Aspect aspect : Aspect.values()) {
			int amount = get(aspect);
			if (amount <= 0) {
				continue;
			}
			if (!first) {
				line.append("  ");
			}
			line.append(aspect.displayName().withStyle(style -> style.withColor(aspect.getColor() & 0xFFFFFF))).append(" " + amount);
			first = false;
		}
		return line;
	}

	public static final class Builder {
		private final int[] amounts = new int[Aspect.COUNT];

		public Builder add(Aspect aspect, int amount) {
			amounts[aspect.ordinal()] += amount;
			return this;
		}

		public AspectList build() {
			return new AspectList(amounts.clone());
		}
	}
}
