package io.github.timekeeperbit.athanor.opus;

import io.github.timekeeperbit.athanor.registry.ModItems;
import io.github.timekeeperbit.athanor.world.Celestial;
import io.github.timekeeperbit.athanor.world.Heat;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The four stages of the Great Work, carried out in the Hermetic Vessel. Each stage needs its own conditions:
 * blackening over fire, whitening under the night sky, yellowing under the day sky, reddening over fire in the open.
 */
public enum OpusStage {
	NIGREDO(true, false, Celestial.Time.ANY, 0),
	ALBEDO(false, true, Celestial.Time.NIGHT, 0),
	CITRINITAS(false, true, Celestial.Time.DAY, 0),
	RUBEDO(true, true, Celestial.Time.ANY, 30);

	public static final int STAGE_TICKS = 600;

	private final boolean heat;
	private final boolean sky;
	private final Celestial.Time time;
	private final int aether;

	OpusStage(boolean heat, boolean sky, Celestial.Time time, int aether) {
		this.heat = heat;
		this.sky = sky;
		this.time = time;
		this.aether = aether;
	}

	public Item input() {
		return switch (this) {
			case NIGREDO -> ModItems.PRIMA_MATERIA;
			case ALBEDO -> ModItems.NIGREDO;
			case CITRINITAS -> ModItems.ALBEDO;
			case RUBEDO -> ModItems.CITRINITAS;
		};
	}

	public Item output() {
		return switch (this) {
			case NIGREDO -> ModItems.NIGREDO;
			case ALBEDO -> ModItems.ALBEDO;
			case CITRINITAS -> ModItems.CITRINITAS;
			case RUBEDO -> ModItems.RUBEDO;
		};
	}

	public boolean needsHeat() {
		return heat;
	}

	public boolean needsSky() {
		return sky;
	}

	public Celestial.Time time() {
		return time;
	}

	/** Aether spent from the chunk when the stage completes. */
	public int aether() {
		return aether;
	}

	public static OpusStage forInput(ItemStack stack) {
		for (OpusStage stage : values()) {
			if (!stack.isEmpty() && stack.is(stage.input())) {
				return stage;
			}
		}
		return null;
	}

	public boolean heatMet(ServerLevel level, BlockPos pos) {
		return !heat || Heat.below(level, pos);
	}

	public boolean skyMet(ServerLevel level, BlockPos pos) {
		return !sky || Celestial.openSky(level, pos);
	}

	public boolean timeMet(ServerLevel level) {
		return time.test(Celestial.isDay(level));
	}

	public boolean conditionsMet(ServerLevel level, BlockPos pos) {
		return heatMet(level, pos) && skyMet(level, pos) && timeMet(level);
	}

	public Component displayName() {
		return Component.translatable("opus.athanor." + name().toLowerCase(Locale.ROOT));
	}
}
