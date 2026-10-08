package io.github.timekeeperbit.athanor.block;

import io.github.timekeeperbit.athanor.registry.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The 3x3x3 Athanor. The core sits in the middle of the front face; the structure extends three blocks behind it.
 * Local coordinates: x = -1..1 to the core's right, y = -1..1 up, z = 0..2 backwards.
 */
public final class AthanorStructure {
	public enum Part {
		BRICKS(state -> state.is(ModBlocks.ATHANOR_BRICKS), ModBlocks.ATHANOR_BRICKS),
		GLASS(state -> state.is(ModBlocks.ALCHEMICAL_GLASS), ModBlocks.ALCHEMICAL_GLASS),
		HEAT(state -> state.is(Blocks.MAGMA_BLOCK), Blocks.MAGMA_BLOCK),
		AIR(BlockState::isAir, Blocks.AIR);

		private final Predicate<BlockState> test;
		private final Block block;

		Part(Predicate<BlockState> test, Block block) {
			this.test = test;
			this.block = block;
		}

		public boolean matches(BlockState state) {
			return test.test(state);
		}

		public Block block() {
			return block;
		}

		public Component displayName() {
			return block.getName();
		}
	}

	public record Entry(BlockPos pos, Part part) {
	}

	private AthanorStructure() {
	}

	/** The part expected at a local position, or null for the core itself. */
	static Part partAt(int x, int y, int z) {
		if (x == 0 && y == 0 && z == 0) {
			return null;
		}
		if (y == -1 && x == 0 && z == 1) {
			return Part.HEAT;
		}
		if (y == 0 && z == 1) {
			return x == 0 ? Part.AIR : Part.GLASS;
		}
		return Part.BRICKS;
	}

	/** Every required block with its world position, for a core at corePos facing the given direction. */
	public static List<Entry> layout(BlockPos corePos, Direction facing) {
		Direction back = facing.getOpposite();
		Direction right = facing.getCounterClockWise();
		List<Entry> entries = new ArrayList<>();
		for (int y = -1; y <= 1; y++) {
			for (int z = 0; z <= 2; z++) {
				for (int x = -1; x <= 1; x++) {
					Part part = partAt(x, y, z);
					if (part != null) {
						entries.add(new Entry(corePos.relative(right, x).relative(back, z).above(y), part));
					}
				}
			}
		}
		return entries;
	}

	/** The first wrong position, or null when the structure is complete. */
	public static Entry findProblem(Level level, BlockPos corePos, Direction facing) {
		for (Entry entry : layout(corePos, facing)) {
			if (!entry.part().matches(level.getBlockState(entry.pos()))) {
				return entry;
			}
		}
		return null;
	}

	/** Builds the structure around an existing core position. Used by tests. */
	public static void build(Level level, BlockPos corePos, Direction facing) {
		for (Entry entry : layout(corePos, facing)) {
			level.setBlockAndUpdate(entry.pos(), entry.part().block().defaultBlockState());
		}
	}
}
