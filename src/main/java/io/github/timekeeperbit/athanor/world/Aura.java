package io.github.timekeeperbit.athanor.world;

import io.github.timekeeperbit.athanor.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * World rule: every chunk holds aether, which rituals and some tools spend, and miasma, which alchemy leaves behind.
 * Aether slowly returns to the chunk's base value; miasma slowly fades. A few chunks are ley nodes with much more aether.
 */
public final class Aura {
	public static final int NODE_BASE = 250;
	/** Miasma at which players start to feel ill, are poisoned by it, and the land begins to wither. */
	public static final int MILD = 40;
	public static final int STRONG = 100;
	public static final int SEVERE = 200;

	private Aura() {
	}

	/** True for roughly one chunk in 24. */
	public static boolean isNode(BlockPos pos) {
		return Math.floorMod(hash(pos), 24) == 0;
	}

	/** Aether the chunk regenerates to: 70 to 130, or {@link #NODE_BASE} on a ley node. */
	public static int base(BlockPos pos) {
		return isNode(pos) ? NODE_BASE : 70 + Math.floorMod(hash(pos) >> 5, 61);
	}

	private static int hash(BlockPos pos) {
		long h = (pos.getX() >> 4) * 341873128712L + (pos.getZ() >> 4) * 132897987541L;
		h ^= h >>> 29;
		h *= 0xBF58476D1CE4E5B9L;
		h ^= h >>> 32;
		return (int) h;
	}

	public static AuraState get(ServerLevel level, BlockPos pos) {
		AuraState stored = level.getChunkAt(pos).getAttached(ModAttachments.AURA);
		long now = level.getGameTime();
		if (stored == null) {
			return new AuraState(base(pos), 0, now);
		}
		return stored.settle(now, base(pos));
	}

	public static void set(ServerLevel level, BlockPos pos, AuraState state) {
		level.getChunkAt(pos).setAttached(ModAttachments.AURA, state);
	}

	/** Spends aether if the chunk has enough. Returns whether it was spent. */
	public static boolean consume(ServerLevel level, BlockPos pos, int amount) {
		AuraState state = get(level, pos);
		if (state.aether() < amount) {
			return false;
		}
		set(level, pos, state.withAether(state.aether() - amount));
		return true;
	}

	public static void addMiasma(ServerLevel level, BlockPos pos, int amount) {
		AuraState state = get(level, pos);
		set(level, pos, state.withMiasma(state.miasma() + amount));
	}

	/** Removes up to {@code amount} miasma and returns how much was removed. */
	public static int purify(ServerLevel level, BlockPos pos, int amount) {
		AuraState state = get(level, pos);
		int removed = Math.min(amount, state.miasma());
		if (removed > 0) {
			set(level, pos, state.withMiasma(state.miasma() - removed));
		}
		return removed;
	}
}
