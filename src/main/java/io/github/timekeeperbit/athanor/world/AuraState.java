package io.github.timekeeperbit.athanor.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Aether and miasma of one chunk, as last written at {@code updated} (game time). Regeneration and decay are applied
 * lazily by {@link #settle}, so chunks nobody visits cost nothing.
 */
public record AuraState(int aether, int miasma, long updated) {
	/** Ticks for one point of aether to return. */
	public static final int REGEN_TICKS = 200;
	/** Ticks for one point of miasma to fade. */
	public static final int DECAY_TICKS = 600;
	public static final int MAX_MIASMA = 500;

	public static final Codec<AuraState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("aether").forGetter(AuraState::aether),
			Codec.INT.fieldOf("miasma").forGetter(AuraState::miasma),
			Codec.LONG.fieldOf("updated").forGetter(AuraState::updated)
	).apply(instance, AuraState::new));

	/**
	 * The state at time {@code now}, with aether regenerated up to {@code base} and miasma decayed. Steps are counted
	 * on fixed boundaries of game time, so frequent reads never lose partial periods.
	 */
	public AuraState settle(long now, int base) {
		if (now <= updated) {
			return this;
		}
		long regen = Math.floorDiv(now, REGEN_TICKS) - Math.floorDiv(updated, REGEN_TICKS);
		long decay = Math.floorDiv(now, DECAY_TICKS) - Math.floorDiv(updated, DECAY_TICKS);
		long newAether = aether >= base ? aether : Math.min(base, aether + regen);
		long newMiasma = Math.max(0, miasma - decay);
		return new AuraState((int) newAether, (int) newMiasma, now);
	}

	public AuraState withAether(int value) {
		return new AuraState(Math.max(0, value), miasma, updated);
	}

	public AuraState withMiasma(int value) {
		return new AuraState(aether, Math.max(0, Math.min(MAX_MIASMA, value)), updated);
	}
}
