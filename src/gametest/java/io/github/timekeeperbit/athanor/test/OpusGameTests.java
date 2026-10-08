package io.github.timekeeperbit.athanor.test;

import io.github.timekeeperbit.athanor.alembic.AlembicBlockEntity;
import io.github.timekeeperbit.athanor.item.AlkahestItem;
import io.github.timekeeperbit.athanor.item.ArcaniumTools;
import io.github.timekeeperbit.athanor.item.DrinkItem;
import io.github.timekeeperbit.athanor.item.EmeraldTabletItem;
import io.github.timekeeperbit.athanor.item.OuroborosRingItem;
import io.github.timekeeperbit.athanor.item.PhilosophersStoneItem;
import io.github.timekeeperbit.athanor.item.QuicksilverMirrorItem;
import io.github.timekeeperbit.athanor.opus.HermeticVesselBlockEntity;
import io.github.timekeeperbit.athanor.registry.ModBlocks;
import io.github.timekeeperbit.athanor.registry.ModEffects;
import io.github.timekeeperbit.athanor.registry.ModItems;
import io.github.timekeeperbit.athanor.ritual.PedestalBlockEntity;
import io.github.timekeeperbit.athanor.ritual.RitualAltarBlockEntity;
import io.github.timekeeperbit.athanor.ritual.RitualRecipe;
import io.github.timekeeperbit.athanor.ritual.RitualRecipes;
import io.github.timekeeperbit.athanor.aspect.Aspect;
import io.github.timekeeperbit.athanor.world.Aura;
import io.github.timekeeperbit.athanor.world.AuraState;
import io.github.timekeeperbit.athanor.world.Celestial;
import io.github.timekeeperbit.athanor.world.WorldGen;
import io.github.timekeeperbit.athanor.world.WorldRules;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.resources.ResourceKey;

public class OpusGameTests {
	private static final BlockPos ALTAR = new BlockPos(4, 1, 4);

	/** Resets the aura of the chunk containing the relative position to full aether and no miasma. */
	private static BlockPos resetAura(GameTestHelper helper, BlockPos relative) {
		BlockPos pos = helper.absolutePos(relative);
		ServerLevel level = helper.getLevel();
		Aura.set(level, pos, new AuraState(Aura.base(pos), 0, level.getGameTime()));
		return pos;
	}

	private static Player playerAt(GameTestHelper helper, BlockPos relative) {
		Player player = helper.makeMockPlayer(GameType.SURVIVAL);
		BlockPos at = helper.absolutePos(relative);
		player.setPos(at.getX() + 0.5, at.getY(), at.getZ() + 0.5);
		return player;
	}

	@GameTest
	public void auraRegeneratesOnFixedSteps(GameTestHelper helper) {
		AuraState start = new AuraState(10, 50, 0);
		AuraState later = start.settle(AuraState.REGEN_TICKS * 5L, 100);
		helper.assertValueEqual(later.aether(), 15, "aether after five periods");
		helper.assertValueEqual(later.miasma(), 49, "miasma after one decay period");
		AuraState stepwise = start.settle(AuraState.REGEN_TICKS - 1, 100);
		helper.assertValueEqual(stepwise.aether(), 10, "no regeneration before the boundary");
		helper.assertValueEqual(stepwise.settle(AuraState.REGEN_TICKS, 100).aether(), 11, "frequent reads keep partial periods");
		helper.assertValueEqual(new AuraState(120, 0, 0).settle(100000, 100).aether(), 120, "aether above base is kept");
		helper.succeed();
	}

	@GameTest
	public void auraIsSpentAndCleansed(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
		Aura.set(level, pos, new AuraState(50, 0, level.getGameTime()));
		helper.assertTrue(Aura.consume(level, pos, 30), "first spend succeeds");
		helper.assertValueEqual(Aura.get(level, pos).aether(), 20, "aether left");
		helper.assertFalse(Aura.consume(level, pos, 30), "second spend fails");
		Aura.addMiasma(level, pos, 10);
		helper.assertValueEqual(Aura.purify(level, pos, 4), 4, "purified");
		helper.assertValueEqual(Aura.get(level, pos).miasma(), 6, "miasma left");
		resetAura(helper, new BlockPos(1, 1, 1));
		helper.assertTrue(Aura.base(pos) >= 70 && Aura.base(pos) <= Aura.NODE_BASE, "base in range");
		helper.succeed();
	}

	private static RitualAltarBlockEntity buildRitual(GameTestHelper helper, RitualRecipe recipe) {
		helper.setBlock(ALTAR, ModBlocks.RITUAL_ALTAR);
		RitualAltarBlockEntity altar = helper.getBlockEntity(ALTAR, RitualAltarBlockEntity.class);
		altar.setStack(new ItemStack(recipe.center()));
		List<Item> items = recipe.pedestals();
		for (int i = 0; i < items.size(); i++) {
			int[] offset = RitualAltarBlockEntity.RING[i];
			BlockPos pos = ALTAR.offset(offset[0], 0, offset[1]);
			helper.setBlock(pos, ModBlocks.PEDESTAL);
			helper.getBlockEntity(pos, PedestalBlockEntity.class).setStack(new ItemStack(items.get(i)));
		}
		return altar;
	}

	@GameTest
	public void ritualNeedsAether(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		RitualRecipe recipe = RitualRecipes.all().get(0);
		RitualAltarBlockEntity altar = buildRitual(helper, recipe);
		BlockPos pos = helper.absolutePos(ALTAR);
		Aura.set(level, pos, new AuraState(0, 0, level.getGameTime()));
		altar.tryStart();
		helper.assertFalse(altar.isActive(), "no aether, no ritual");
		resetAura(helper, ALTAR);
		int before = Aura.get(level, pos).aether();
		altar.tryStart();
		helper.assertTrue(altar.isActive(), "ritual starts with aether");
		helper.assertValueEqual(Aura.get(level, pos).aether(), before - recipe.aether(), "aether spent");
		resetAura(helper, ALTAR);
		helper.succeed();
	}

	@GameTest
	public void ritualRespectsTimeOfDay(GameTestHelper helper) {
		Celestial.Time forbidden = Celestial.isDay(helper.getLevel()) ? Celestial.Time.NIGHT : Celestial.Time.DAY;
		RitualRecipe recipe = RitualRecipes.all().stream().filter(r -> r.time() == forbidden && r.pedestals().size() <= 8)
				.findFirst().orElseThrow();
		RitualAltarBlockEntity altar = buildRitual(helper, recipe);
		resetAura(helper, ALTAR);
		altar.tryStart();
		helper.assertFalse(altar.isActive(), "ritual must wait for " + forbidden);
		helper.succeed();
	}

	@GameTest
	public void ritualCostsFitTheWorld(GameTestHelper helper) {
		for (RitualRecipe recipe : RitualRecipes.all()) {
			helper.assertTrue(recipe.pedestals().size() <= RitualAltarBlockEntity.RING.length, "too many pedestals for " + recipe.result());
			if (recipe.aether() != RitualRecipes.GRAND_RITE_AETHER) {
				helper.assertTrue(recipe.aether() <= RitualRecipes.MAX_ORDINARY_AETHER, "ritual for " + recipe.result() + " costs too much");
			}
		}
		helper.assertTrue(RitualRecipes.GRAND_RITE_AETHER > 130 && RitualRecipes.GRAND_RITE_AETHER <= Aura.NODE_BASE, "grand rite needs a ley node");
		helper.succeed();
	}

	@GameTest(maxTicks = 200)
	public void alembicDistilsCinnabar(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 2, 1);
		helper.setBlock(pos.below(), Blocks.MAGMA_BLOCK);
		helper.setBlock(pos, ModBlocks.ALEMBIC);
		AlembicBlockEntity alembic = helper.getBlockEntity(pos, AlembicBlockEntity.class);
		alembic.setItem(AlembicBlockEntity.INPUT, new ItemStack(ModItems.CINNABAR));
		helper.succeedWhen(() -> {
			helper.assertTrue(alembic.getItem(AlembicBlockEntity.PRODUCT).is(ModItems.QUICKSILVER), "quicksilver distilled");
			helper.assertTrue(alembic.getItem(AlembicBlockEntity.BYPRODUCT).is(ModItems.SULFUR), "sulfur left behind");
			helper.assertTrue(alembic.getItem(AlembicBlockEntity.INPUT).isEmpty(), "cinnabar consumed");
		});
	}

	@GameTest(maxTicks = 200)
	public void alembicNeedsHeat(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 2, 1);
		helper.setBlock(pos.below(), Blocks.STONE);
		helper.setBlock(pos, ModBlocks.ALEMBIC);
		AlembicBlockEntity alembic = helper.getBlockEntity(pos, AlembicBlockEntity.class);
		alembic.setItem(AlembicBlockEntity.INPUT, new ItemStack(ModItems.CINNABAR));
		helper.runAfterDelay(AlembicBlockEntity.WORK_TICKS + 30, () -> {
			helper.assertTrue(alembic.getItem(AlembicBlockEntity.INPUT).is(ModItems.CINNABAR), "nothing distilled without heat");
			helper.assertFalse(alembic.isHeated(), "not heated");
			helper.succeed();
		});
	}

	@GameTest(maxTicks = 700)
	public void vesselBlackensPrimaMateriaOverHeat(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 2, 1);
		helper.setBlock(pos.below(), Blocks.MAGMA_BLOCK);
		helper.setBlock(pos, ModBlocks.HERMETIC_VESSEL);
		HermeticVesselBlockEntity vessel = helper.getBlockEntity(pos, HermeticVesselBlockEntity.class);
		vessel.setStack(new ItemStack(ModItems.PRIMA_MATERIA));
		helper.succeedWhen(() -> helper.assertTrue(vessel.getStack().is(ModItems.NIGREDO), "prima materia should blacken"));
	}

	@GameTest(maxTicks = 200)
	public void vesselWaitsWithoutHeat(GameTestHelper helper) {
		BlockPos pos = new BlockPos(1, 2, 1);
		helper.setBlock(pos.below(), Blocks.STONE);
		helper.setBlock(pos, ModBlocks.HERMETIC_VESSEL);
		HermeticVesselBlockEntity vessel = helper.getBlockEntity(pos, HermeticVesselBlockEntity.class);
		vessel.setStack(new ItemStack(ModItems.PRIMA_MATERIA));
		helper.runAfterDelay(100, () -> {
			helper.assertValueEqual(vessel.getProgress(), 0, "no progress without heat");
			helper.assertTrue(vessel.getStack().is(ModItems.PRIMA_MATERIA), "still prima materia");
			helper.succeed();
		});
	}

	@GameTest(maxTicks = 250)
	public void saltLampCleansMiasma(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos rel = new BlockPos(1, 1, 1);
		BlockPos pos = helper.absolutePos(rel);
		Aura.set(level, pos, new AuraState(Aura.base(pos), 50, level.getGameTime()));
		helper.setBlock(rel, ModBlocks.SALT_LAMP);
		helper.succeedWhen(() -> helper.assertTrue(Aura.get(level, pos).miasma() < 50, "lamp should cleanse"));
	}

	@GameTest
	public void alkahestDissolvesBlocks(GameTestHelper helper) {
		BlockPos rel = new BlockPos(1, 1, 1);
		helper.setBlock(rel, Blocks.COBBLESTONE);
		helper.assertTrue(AlkahestItem.dissolve(helper.getLevel(), helper.absolutePos(rel)), "cobblestone dissolves");
		helper.assertBlockPresent(Blocks.AIR, rel);
		helper.assertItemEntityPresent(ModItems.CRYSTALS.get(Aspect.TERRA), rel, 2.0);
		helper.setBlock(rel, Blocks.BEDROCK);
		helper.assertFalse(AlkahestItem.dissolve(helper.getLevel(), helper.absolutePos(rel)), "bedrock resists");
		resetAura(helper, rel);
		helper.succeed();
	}

	@GameTest
	public void mirrorReturnsToBoundPlace(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		resetAura(helper, new BlockPos(1, 1, 1));
		Player player = playerAt(helper, new BlockPos(1, 1, 1));
		ItemStack mirror = new ItemStack(ModItems.QUICKSILVER_MIRROR);
		helper.assertValueEqual(QuicksilverMirrorItem.travel(mirror, level, player), "message.athanor.mirror.unbound", "unbound mirror");
		BlockPos target = helper.absolutePos(new BlockPos(3, 1, 3));
		QuicksilverMirrorItem.bind(mirror, level, target);
		helper.assertValueEqual(QuicksilverMirrorItem.travel(mirror, level, player), "message.athanor.mirror.travelled", "travelled");
		helper.assertValueEqual(player.blockPosition(), target, "player position");
		resetAura(helper, new BlockPos(1, 1, 1));
		helper.succeed();
	}

	@GameTest
	public void ouroborosRingMendsWithExperience(GameTestHelper helper) {
		Player player = playerAt(helper, new BlockPos(1, 1, 1));
		ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);
		pickaxe.setDamageValue(10);
		player.getInventory().add(pickaxe);
		player.giveExperiencePoints(5);
		helper.assertValueEqual(OuroborosRingItem.mend(player), 1, "one item mended");
		helper.assertValueEqual(pickaxe.getDamageValue(), 9, "one point mended");
		helper.assertValueEqual(player.totalExperience, 4, "one experience point paid");
		helper.succeed();
	}

	@GameTest
	public void arcaniumToolsMendWithAether(GameTestHelper helper) {
		resetAura(helper, new BlockPos(1, 1, 1));
		Player player = playerAt(helper, new BlockPos(1, 1, 1));
		ItemStack pickaxe = new ItemStack(ModItems.ARCANIUM_PICKAXE);
		pickaxe.setDamageValue(100);
		player.getInventory().add(pickaxe);
		helper.assertValueEqual(ArcaniumTools.selfRepair(helper.getLevel(), player), 1, "one tool mended");
		helper.assertValueEqual(pickaxe.getDamageValue(), 100 - ArcaniumTools.MEND, "mended amount");
		helper.assertValueEqual(pickaxe.getMaxDamage(), ArcaniumTools.MATERIAL.durability(), "durability");
		resetAura(helper, new BlockPos(1, 1, 1));
		helper.succeed();
	}

	@GameTest
	public void elixirCheatsDeathOnce(GameTestHelper helper) {
		Player player = playerAt(helper, new BlockPos(1, 1, 1));
		((DrinkItem) ModItems.ELIXIR_OF_LIFE).apply(helper.getLevel(), player);
		helper.assertTrue(player.hasEffect(ModEffects.AETERNITAS), "elixir grants aeternitas");
		helper.assertTrue(WorldRules.cheatDeath(player), "first death is cheated");
		helper.assertFalse(player.hasEffect(ModEffects.AETERNITAS), "aeternitas is used up");
		helper.assertTrue(player.getHealth() >= player.getMaxHealth() / 2 - 0.01F, "revived at half health");
		helper.assertFalse(WorldRules.cheatDeath(player), "second death is real");
		helper.succeed();
	}

	@GameTest
	public void panaceaCuresAndWardsMiasma(GameTestHelper helper) {
		Player player = playerAt(helper, new BlockPos(1, 1, 1));
		player.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
		player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200));
		((DrinkItem) ModItems.PANACEA).apply(helper.getLevel(), player);
		helper.assertFalse(player.hasEffect(MobEffects.POISON), "poison cured");
		helper.assertTrue(player.hasEffect(MobEffects.SPEED), "beneficial effects kept");
		helper.assertTrue(player.hasEffect(ModEffects.PURITY), "purity granted");
		WorldRules.afflict(player, 300, false);
		helper.assertFalse(player.hasEffect(MobEffects.WEAKNESS), "purity wards off miasma");
		helper.succeed();
	}

	@GameTest
	public void miasmaSickensAndWithers(GameTestHelper helper) {
		Player player = playerAt(helper, new BlockPos(1, 1, 1));
		WorldRules.afflict(player, Aura.MILD - 1, false);
		helper.assertFalse(player.hasEffect(MobEffects.WEAKNESS), "clean air is harmless");
		WorldRules.afflict(player, Aura.STRONG, false);
		helper.assertTrue(player.hasEffect(MobEffects.WEAKNESS), "weakness");
		helper.assertTrue(player.hasEffect(MobEffects.HUNGER), "hunger");
		BlockPos grass = new BlockPos(2, 1, 2);
		helper.setBlock(grass, Blocks.GRASS_BLOCK);
		helper.assertTrue(WorldRules.witherAt(helper.getLevel(), helper.absolutePos(grass)), "grass withers");
		helper.assertBlockPresent(Blocks.COARSE_DIRT, grass);
		helper.succeed();
	}

	@GameTest
	public void newBlocksHaveLootAndOresGenerate(GameTestHelper helper) {
		ServerLevel level = helper.getLevel();
		BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
		Map<Block, Item> expected = Map.ofEntries(
				Map.entry(ModBlocks.CINNABAR_ORE, ModItems.CINNABAR), Map.entry(ModBlocks.DEEPSLATE_CINNABAR_ORE, ModItems.CINNABAR),
				Map.entry(ModBlocks.SILVER_ORE, ModItems.RAW_SILVER), Map.entry(ModBlocks.DEEPSLATE_SILVER_ORE, ModItems.RAW_SILVER),
				Map.entry(ModBlocks.LEAD_ORE, ModItems.RAW_LEAD), Map.entry(ModBlocks.DEEPSLATE_LEAD_ORE, ModItems.RAW_LEAD),
				Map.entry(ModBlocks.ROCK_SALT, ModItems.SALT));
		for (Map.Entry<Block, Item> entry : expected.entrySet()) {
			List<ItemStack> drops = Block.getDrops(entry.getKey().defaultBlockState(), level, pos, null);
			helper.assertTrue(drops.stream().anyMatch(stack -> stack.is(entry.getValue())), entry.getKey() + " should drop " + entry.getValue());
		}
		for (Block block : List.of(ModBlocks.ATHANOR_BRICKS, ModBlocks.RESOLVER, ModBlocks.ATHANOR_CORE, ModBlocks.RITUAL_ALTAR, ModBlocks.PEDESTAL,
				ModBlocks.ALEMBIC, ModBlocks.HERMETIC_VESSEL, ModBlocks.SALT_LAMP, ModBlocks.SILVER_BLOCK, ModBlocks.LEAD_BLOCK)) {
			List<ItemStack> drops = Block.getDrops(block.defaultBlockState(), level, pos, null);
			helper.assertTrue(drops.stream().anyMatch(stack -> stack.is(block.asItem())), block + " should drop itself");
		}
		for (ResourceKey<PlacedFeature> ore : WorldGen.ORES) {
			helper.assertTrue(level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE).get(ore).isPresent(), "missing feature " + ore);
		}
		helper.succeed();
	}

	@GameTest
	public void tabletAndTransmutations(GameTestHelper helper) {
		ItemStack tablet = new ItemStack(ModItems.EMERALD_TABLET);
		helper.assertValueEqual(tablet.get(DataComponents.WRITTEN_BOOK_CONTENT).pages().size(), EmeraldTabletItem.PAGES, "tablet pages");
		helper.assertTrue(PhilosophersStoneItem.transmutations().get(ModBlocks.LEAD_ORE) == ModBlocks.SILVER_ORE, "lead becomes silver");
		helper.assertTrue(PhilosophersStoneItem.transmutations().get(ModBlocks.SILVER_ORE) == Blocks.GOLD_ORE, "silver becomes gold");
		helper.succeed();
	}
}
