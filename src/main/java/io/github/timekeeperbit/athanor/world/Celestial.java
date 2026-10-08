package io.github.timekeeperbit.athanor.world;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/** World rule: some works only succeed under the sun or the moon, and the full moon speeds rituals. */
public final class Celestial {
	private Celestial() {
	}

	public enum Time {
		ANY, DAY, NIGHT;

		public boolean test(boolean day) {
			return this == ANY || (this == DAY) == day;
		}

		public Component describe() {
			return Component.translatable("celestial.athanor." + name().toLowerCase(java.util.Locale.ROOT));
		}
	}

	public static boolean isDay(ServerLevel level) {
		return level.isBrightOutside();
	}

	/** True at night under a full moon. */
	public static boolean isFullMoonNight(ServerLevel level, BlockPos pos) {
		return !level.isBrightOutside() && level.getMoonBrightness(pos) >= 0.99F;
	}

	public static boolean openSky(ServerLevel level, BlockPos pos) {
		return level.canSeeSky(pos.above());
	}
}
