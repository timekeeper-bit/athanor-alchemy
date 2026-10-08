package io.github.timekeeperbit.athanor.registry;

import io.github.timekeeperbit.athanor.Athanor;
import io.github.timekeeperbit.athanor.effect.AlchemyEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class ModEffects {
	/** Saves the bearer from one death, then ends. */
	public static final Holder<MobEffect> AETERNITAS = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Athanor.id("aeternitas"),
			new AlchemyEffect(MobEffectCategory.BENEFICIAL, 0xD02838));
	/** Protects from miasma. */
	public static final Holder<MobEffect> PURITY = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Athanor.id("purity"),
			new AlchemyEffect(MobEffectCategory.BENEFICIAL, 0x5AD28C));

	private ModEffects() {
	}

	public static void init() {
	}
}
