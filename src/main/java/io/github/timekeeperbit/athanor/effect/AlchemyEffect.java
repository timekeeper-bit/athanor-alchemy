package io.github.timekeeperbit.athanor.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/** A status effect whose behaviour lives in event handlers rather than in ticks. */
public class AlchemyEffect extends MobEffect {
	public AlchemyEffect(MobEffectCategory category, int color) {
		super(category, color);
	}
}
