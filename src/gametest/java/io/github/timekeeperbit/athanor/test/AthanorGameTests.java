package io.github.timekeeperbit.athanor.test;

import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.aspect.AspectList;
import io.github.timekeeperbit.athanor.aspect.AspectTable;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlock;
import io.github.timekeeperbit.athanor.block.AthanorCoreBlockEntity;
import io.github.timekeeperbit.athanor.block.AthanorStructure;
import io.github.timekeeperbit.athanor.block.ResolverBlockEntity;
import io.github.timekeeperbit.athanor.item.ItemMagnetItem;
import io.github.timekeeperbit.athanor.item.OreMagnetItem;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipe;
import io.github.timekeeperbit.athanor.recipe.AthanorRecipes;
import io.github.timekeeperbit.athanor.registry.ModBlocks;
import io.github.timekeeperbit.athanor.registry.ModItems;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;

public class AthanorGameTests {
	private static final BlockPos CORE = new BlockPos(3, 2, 1);

	@GameTest
	public void everyRecipeIsLossy(GameTestHelper helper) {
		Set<Item> catalysts = new HashSet<>();
		for (AthanorRecipe recipe : AthanorRecipes.all()) {
			helper.assertTrue(catalysts.add(recipe.catalyst()), "Duplicate catalyst " + recipe.catalyst());
			helper.assertTrue(recipe.cost().total() > 0, "Recipe without cost for " + recipe.result());
			int in = AspectTable.get(new ItemStack(recipe.catalyst())).total() + recipe.cost().total();
			int out = AspectTable.get(recipe.resultStack()).total() * recipe.count();
			helper.assertTrue(out < in, "Recipe for " + recipe.result() + " would create aspects: in " + in + ", out " + out);
		}
		helper.succeed();
	}

	@GameTest
	public void aspectTableCoversTags(GameTestHelper helper) {
		helper.assertTrue(AspectTable.has(new ItemStack(Items.OAK_LOG)), "Logs should have aspects through the tag");
		helper.assertFalse(AspectTable.has(new ItemStack(ModItems.CRYSTALS.get(Aspect.TERRA))), "Crystals must not be resolvable");
		AspectList cobble = AspectTable.get(new ItemStack(Items.COBBLESTONE));
		helper.assertValueEqual(cobble.get(Aspect.TERRA), 1, "cobblestone terra");
		helper.assertValueEqual(cobble.get(Aspect.PERDITIO), 1, "cobblestone perditio");
		helper.succeed();
	}

	@GameTest(maxTicks = 200)
	public void resolverBreaksItemsIntoCrystals(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, ModBlocks.RESOLVER);
		ResolverBlockEntity resolver = helper.getBlockEntity(pos, ResolverBlockEntity.class);
		resolver.setItem(ResolverBlockEntity.INPUT, new ItemStack(Items.COBBLESTONE, 2));
		helper.succeedWhen(() -> {
			helper.assertValueEqual(resolver.getItem(ResolverBlockEntity.slotFor(Aspect.TERRA)).getCount(), 2, "terra crystals");
			helper.assertValueEqual(resolver.getItem(ResolverBlockEntity.slotFor(Aspect.PERDITIO)).getCount(), 2, "perditio crystals");
			helper.assertTrue(resolver.getItem(ResolverBlockEntity.INPUT).isEmpty(), "input consumed");
		});
	}

	@GameTest(maxTicks = 200)
	public void resolverRejectsUnknownItems(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 1, 1);
		helper.setBlock(pos, ModBlocks.RESOLVER);
		ResolverBlockEntity resolver = helper.getBlockEntity(pos, ResolverBlockEntity.class);
		helper.assertFalse(resolver.canPlaceItem(ResolverBlockEntity.INPUT, new ItemStack(Items.ELYTRA)), "elytra has no aspects");
		helper.succeed();
	}

	private static AthanorCoreBlockEntity buildAthanor(GameTestHelper helper) {
		helper.setBlock(CORE, ModBlocks.ATHANOR_CORE.defaultBlockState().setValue(AthanorCoreBlock.FACING, Direction.NORTH));
		AthanorStructure.build(helper.getLevel(), helper.absolutePos(CORE), Direction.NORTH);
		return helper.getBlockEntity(CORE, AthanorCoreBlockEntity.class);
	}

	@GameTest(maxTicks = 100)
	public void athanorFormsWhenComplete(GameTestHelper helper) {
		AthanorCoreBlockEntity core = buildAthanor(helper);
		helper.succeedWhen(() -> {
			helper.assertTrue(core.isFormed(), "structure should be formed");
			helper.assertTrue(helper.getLevel().getBlockState(helper.absolutePos(CORE)).getValue(AthanorCoreBlock.FORMED), "formed block state");
		});
	}

	@GameTest(maxTicks = 100)
	public void athanorNeedsHeatSource(GameTestHelper helper) {
		AthanorCoreBlockEntity core = buildAthanor(helper);
		// The magma block sits below the chamber, one block behind the core (north-facing core: south is behind).
		BlockPos heat = CORE.south().below();
		helper.setBlock(heat, Blocks.STONE);
		helper.assertTrue(AthanorStructure.findProblem(helper.getLevel(), helper.absolutePos(CORE), Direction.NORTH) != null, "problem expected");
		helper.runAfterDelay(40, () -> {
			helper.assertFalse(core.isFormed(), "structure must not form without heat");
			helper.succeed();
		});
	}

	@GameTest(maxTicks = 300)
	public void athanorMultipliesIron(GameTestHelper helper) {
		AthanorCoreBlockEntity core = buildAthanor(helper);
		core.setItem(AthanorCoreBlockEntity.ESSENCE, new ItemStack(ModItems.CRYSTALS.get(Aspect.METALLUM), 4));
		core.setPool(Aspect.ORDO, 2);
		core.setItem(AthanorCoreBlockEntity.CATALYST, new ItemStack(Items.IRON_INGOT));
		helper.succeedWhen(() -> {
			ItemStack out = core.getItem(AthanorCoreBlockEntity.OUTPUT);
			helper.assertTrue(out.is(Items.IRON_INGOT), "output should be iron");
			helper.assertValueEqual(out.getCount(), 2, "iron count");
			helper.assertValueEqual(core.getPool(Aspect.METALLUM), 0, "metallum consumed");
			helper.assertValueEqual(core.getPool(Aspect.ORDO), 0, "ordo consumed");
			helper.assertTrue(core.getItem(AthanorCoreBlockEntity.CATALYST).isEmpty(), "catalyst consumed");
		});
	}

	@GameTest(maxTicks = 150)
	public void athanorWaitsForAspects(GameTestHelper helper) {
		AthanorCoreBlockEntity core = buildAthanor(helper);
		core.setPool(Aspect.METALLUM, 3);
		core.setItem(AthanorCoreBlockEntity.CATALYST, new ItemStack(Items.IRON_INGOT));
		helper.runAfterDelay(130, () -> {
			helper.assertTrue(core.getItem(AthanorCoreBlockEntity.OUTPUT).isEmpty(), "nothing should be made without enough aspects");
			helper.assertValueEqual(core.getPool(Aspect.METALLUM), 3, "pool untouched");
			helper.succeed();
		});
	}

	@GameTest
	public void oreMagnetMinesOres(GameTestHelper helper) {
		BlockPos center = new BlockPos(3, 2, 3);
		helper.setBlock(center.east(), Blocks.IRON_ORE);
		helper.setBlock(center.above(), Blocks.COAL_ORE);
		helper.setBlock(center.west(), Blocks.STONE);
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		int mined = OreMagnetItem.pullOres(helper.getLevel(), helper.absolutePos(center), 2, player);
		helper.assertValueEqual(mined, 2, "ores mined");
		helper.assertBlockPresent(Blocks.AIR, center.east());
		helper.assertBlockPresent(Blocks.AIR, center.above());
		helper.assertBlockPresent(Blocks.STONE, center.west());
		helper.assertTrue(player.getInventory().countItem(Items.RAW_IRON) >= 1, "raw iron in inventory");
		helper.assertTrue(player.getInventory().countItem(Items.COAL) >= 1, "coal in inventory");
		helper.succeed();
	}

	@GameTest
	public void itemMagnetPullsDrops(GameTestHelper helper) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		ItemStack magnet = new ItemStack(ModItems.ITEM_MAGNET);
		helper.assertFalse(ItemMagnetItem.isOn(magnet), "magnet starts off");
		ItemMagnetItem.setOn(magnet, true);
		helper.assertTrue(ItemMagnetItem.isOn(magnet), "magnet switched on");
		ItemEntity item = new ItemEntity(helper.getLevel(), player.getX() + 4, player.getY(), player.getZ(), new ItemStack(Items.APPLE));
		item.setNoPickUpDelay();
		helper.getLevel().addFreshEntity(item);
		int moved = ItemMagnetItem.pull(helper.getLevel(), player);
		helper.assertTrue(moved >= 1, "at least one entity moved");
		helper.assertTrue(item.distanceToSqr(player) < 1.0, "item is at the player");
		helper.succeed();
	}
}
