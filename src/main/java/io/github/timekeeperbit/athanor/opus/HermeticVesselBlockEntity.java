package io.github.timekeeperbit.athanor.opus;

import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import io.github.timekeeperbit.athanor.ritual.ItemHolderBlockEntity;
import io.github.timekeeperbit.athanor.world.Aura;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** The philosophers' egg. Holds one item and, while its stage's conditions hold, slowly carries it to the next stage. */
public class HermeticVesselBlockEntity extends ItemHolderBlockEntity {
	private int progress;

	public HermeticVesselBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.HERMETIC_VESSEL, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, HermeticVesselBlockEntity vessel) {
		vessel.tick((ServerLevel) level, pos);
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public void setStack(ItemStack newStack) {
		if (!ItemStack.isSameItem(newStack, getStack())) {
			progress = 0;
		}
		super.setStack(newStack);
	}

	private void tick(ServerLevel level, BlockPos pos) {
		tickDisplay(level);
		OpusStage stage = OpusStage.forInput(getStack());
		if (stage == null || !stage.conditionsMet(level, pos)) {
			return;
		}
		if (progress < OpusStage.STAGE_TICKS) {
			progress++;
			setChanged();
			if (progress % 20 == 0) {
				level.sendParticles(particle(stage), pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 2, 0.15, 0.2, 0.15, 0.01);
			}
			return;
		}
		if (stage.aether() > 0 && !Aura.consume(level, pos, stage.aether())) {
			return;
		}
		progress = 0;
		setStack(new ItemStack(stage.output()));
		level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 0.8F + stage.ordinal() * 0.15F);
		level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 20, 0.3, 0.3, 0.3, 0.03);
	}

	private static ParticleOptions particle(OpusStage stage) {
		return switch (stage) {
			case NIGREDO -> ParticleTypes.SMOKE;
			case ALBEDO -> ParticleTypes.SNOWFLAKE;
			case CITRINITAS -> ParticleTypes.WAX_ON;
			case RUBEDO -> ParticleTypes.FLAME;
		};
	}

	/** A chat line describing the stage, its progress and which conditions currently hold. */
	public MutableComponent describe(ServerLevel level) {
		OpusStage stage = OpusStage.forInput(getStack());
		if (stage == null) {
			return Component.translatable("message.athanor.vessel.idle").withStyle(ChatFormatting.GRAY);
		}
		BlockPos pos = getBlockPos();
		MutableComponent line = Component.translatable("message.athanor.vessel.stage", stage.displayName(),
				progress * 100 / OpusStage.STAGE_TICKS).withStyle(ChatFormatting.GOLD);
		if (stage.needsHeat()) {
			line.append(" ").append(check("message.athanor.vessel.heat", stage.heatMet(level, pos)));
		}
		if (stage.needsSky()) {
			line.append(" ").append(check("message.athanor.vessel.sky", stage.skyMet(level, pos)));
		}
		if (stage.time() != io.github.timekeeperbit.athanor.world.Celestial.Time.ANY) {
			line.append(" ").append(check(stage.time().describe(), stage.timeMet(level)));
		}
		if (stage.aether() > 0) {
			line.append(" ").append(check(Component.translatable("message.athanor.vessel.aether", stage.aether()),
					Aura.get(level, pos).aether() >= stage.aether()));
		}
		return line;
	}

	private static Component check(String key, boolean met) {
		return check(Component.translatable(key), met);
	}

	private static Component check(Component label, boolean met) {
		return Component.literal(met ? "✔ " : "✘ ").append(label).withStyle(met ? ChatFormatting.GREEN : ChatFormatting.RED);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		progress = input.getIntOr("Progress", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putInt("Progress", progress);
	}
}
