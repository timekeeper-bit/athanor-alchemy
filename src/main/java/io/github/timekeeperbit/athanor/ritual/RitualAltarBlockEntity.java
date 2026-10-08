package io.github.timekeeperbit.athanor.ritual;

import io.github.timekeeperbit.athanor.registry.ModBlockEntities;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Centre of the infusion ritual. Pedestals must stand on the eight ring positions around the altar, on the same level.
 * A wand starts the ritual; it fails if any pedestal item changes before it completes.
 */
public class RitualAltarBlockEntity extends ItemHolderBlockEntity {
	/** Offsets of the eight pedestal positions: four at distance 3 and four diagonals at (2, 2). */
	public static final int[][] RING = {{3, 0}, {-3, 0}, {0, 3}, {0, -3}, {2, 2}, {2, -2}, {-2, 2}, {-2, -2}};

	private boolean active;
	private int progress;
	private int total;

	public RitualAltarBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.RITUAL_ALTAR, pos, state);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, RitualAltarBlockEntity altar) {
		altar.tick((ServerLevel) level);
	}

	public boolean isActive() {
		return active;
	}

	public List<PedestalBlockEntity> pedestals() {
		List<PedestalBlockEntity> found = new ArrayList<>();
		if (level == null) {
			return found;
		}
		for (int[] offset : RING) {
			if (level.getBlockEntity(getBlockPos().offset(offset[0], 0, offset[1])) instanceof PedestalBlockEntity pedestal) {
				found.add(pedestal);
			}
		}
		return found;
	}

	private List<ItemStack> pedestalStacks() {
		List<ItemStack> stacks = new ArrayList<>();
		for (PedestalBlockEntity pedestal : pedestals()) {
			stacks.add(pedestal.getStack());
		}
		return stacks;
	}

	public RitualRecipe currentRecipe() {
		return RitualRecipes.find(getStack(), pedestalStacks());
	}

	/** Tries to start a ritual. Returns the message to show the player. */
	public Component tryStart() {
		if (active) {
			return Component.translatable("message.athanor.ritual.busy");
		}
		RitualRecipe recipe = currentRecipe();
		if (recipe == null) {
			return Component.translatable("message.athanor.ritual.no_recipe", pedestals().size());
		}
		active = true;
		progress = 0;
		total = recipe.ticks();
		setChanged();
		if (level != null) {
			level.playSound(null, getBlockPos(), SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 0.8F);
		}
		return Component.translatable("message.athanor.ritual.started", recipe.resultStack().getHoverName());
	}

	private void tick(ServerLevel level) {
		tickDisplay(level);
		if (!active) {
			return;
		}
		RitualRecipe recipe = currentRecipe();
		if (recipe == null) {
			fail(level);
			return;
		}
		progress++;
		BlockPos pos = getBlockPos();
		if (progress % 4 == 0) {
			for (PedestalBlockEntity pedestal : pedestals()) {
				if (!pedestal.getStack().isEmpty()) {
					BlockPos from = pedestal.getBlockPos();
					double dx = pos.getX() - from.getX();
					double dz = pos.getZ() - from.getZ();
					// Speed-style particle call: count 0 makes the offsets a direction.
					level.sendParticles(ParticleTypes.ENCHANT, from.getX() + 0.5, from.getY() + 1.3, from.getZ() + 0.5, 0, dx, 0.5, dz, 1.0);
				}
			}
			level.sendParticles(ParticleTypes.WITCH, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 3, 0.3, 0.3, 0.3, 0.0);
		}
		if (progress % 40 == 0) {
			level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 0.8F, 0.6F + progress / (float) Math.max(1, total));
		}
		if (progress >= total) {
			complete(level, recipe);
		}
		setChanged();
	}

	private void complete(ServerLevel level, RitualRecipe recipe) {
		active = false;
		progress = 0;
		for (PedestalBlockEntity pedestal : pedestals()) {
			pedestal.setStack(ItemStack.EMPTY);
		}
		setStack(recipe.resultStack());
		BlockPos pos = getBlockPos();
		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
		if (bolt != null) {
			bolt.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			bolt.setVisualOnly(true);
			level.addFreshEntity(bolt);
		}
		level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 40, 0.5, 0.8, 0.5, 0.05);
	}

	private void fail(ServerLevel level) {
		active = false;
		progress = 0;
		setChanged();
		BlockPos pos = getBlockPos();
		level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 0.6F);
		level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 20, 0.4, 0.4, 0.4, 0.02);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		active = input.getBooleanOr("Active", false);
		progress = input.getIntOr("Progress", 0);
		total = input.getIntOr("Total", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putBoolean("Active", active);
		output.putInt("Progress", progress);
		output.putInt("Total", total);
	}
}
