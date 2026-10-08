package io.github.timekeeperbit.athanor.world;

import io.github.timekeeperbit.athanor.item.ArcaniumTools;
import io.github.timekeeperbit.athanor.registry.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

/** Applies the world rules each couple of seconds: miasma sickness, blight, self-mending tools, and the Aeternitas effect. */
public final class WorldRules {
	public static final int INTERVAL = 40;
	public static final int REPAIR_INTERVAL = 200;

	private WorldRules() {
	}

	public static void init() {
		ServerTickEvents.END_LEVEL_TICK.register(WorldRules::tick);
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> !cheatDeath(entity));
	}

	private static void tick(ServerLevel level) {
		long time = level.getGameTime();
		if (time % INTERVAL != 0) {
			return;
		}
		for (ServerPlayer player : level.players()) {
			if (player.isSpectator()) {
				continue;
			}
			BlockPos pos = player.blockPosition();
			int miasma = Aura.get(level, pos).miasma();
			afflict(player, miasma, time % REPAIR_INTERVAL == 0);
			if (miasma >= Aura.STRONG) {
				blight(level, pos, miasma >= Aura.SEVERE ? 3 : 1);
			}
			if (time % REPAIR_INTERVAL == 0) {
				ArcaniumTools.selfRepair(level, player);
			}
		}
	}

	/** Gives the effects of breathing miasma. Purity protects completely. */
	public static void afflict(LivingEntity entity, int miasma, boolean warn) {
		if (miasma < Aura.MILD || entity.hasEffect(ModEffects.PURITY)) {
			return;
		}
		int duration = INTERVAL * 2 + 20;
		entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, 0, true, true));
		if (miasma >= Aura.STRONG) {
			entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, 0, true, true));
			entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, 0, true, true));
		}
		if (miasma >= Aura.SEVERE) {
			entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0, true, true));
		}
		if (warn && entity instanceof ServerPlayer player) {
			player.sendOverlayMessage(Component.translatable("message.athanor.miasma.warning", miasma).withStyle(ChatFormatting.DARK_GREEN));
		}
	}

	/** Withers the surface near {@code center}: grass turns to coarse dirt and small plants die. */
	public static int blight(ServerLevel level, BlockPos center, int attempts) {
		RandomSource random = level.getRandom();
		int changed = 0;
		for (int i = 0; i < attempts; i++) {
			int x = center.getX() + random.nextInt(17) - 8;
			int z = center.getZ() + random.nextInt(17) - 8;
			BlockPos top = new BlockPos(x, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, z);
			if (witherAt(level, top)) {
				changed++;
			}
		}
		return changed;
	}

	/** Withers one surface block: grass turns to coarse dirt and small plants die. Returns whether anything changed. */
	public static boolean witherAt(ServerLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM)) {
			level.setBlockAndUpdate(pos, Blocks.COARSE_DIRT.defaultBlockState());
		} else if (state.is(BlockTags.SMALL_FLOWERS) || state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		} else {
			return false;
		}
		level.sendParticles(ParticleTypes.SQUID_INK, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 4, 0.3, 0.1, 0.3, 0.0);
		return true;
	}

	/** Saves an entity under Aeternitas from death. Returns true if it was saved. */
	public static boolean cheatDeath(LivingEntity entity) {
		if (!entity.hasEffect(ModEffects.AETERNITAS)) {
			return false;
		}
		entity.removeEffect(ModEffects.AETERNITAS);
		entity.setHealth(entity.getMaxHealth() / 2);
		entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
		entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1));
		if (entity.level() instanceof ServerLevel level) {
			level.playSound(null, entity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
			level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, entity.getX(), entity.getY() + 1.0, entity.getZ(), 40, 0.5, 0.8, 0.5, 0.3);
		}
		return true;
	}
}
