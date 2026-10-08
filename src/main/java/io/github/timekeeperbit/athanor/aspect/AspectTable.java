package io.github.timekeeperbit.athanor.aspect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Aspect values of items. Exact items are checked first, then item tags in order.
 * Values are written as short strings: T terra, A aqua, I ignis, E aer, O ordo, P perditio, V vita, M metallum.
 */
public final class AspectTable {
	private static final Map<Item, AspectList> ITEMS = new IdentityHashMap<>();
	private static final List<Map.Entry<TagKey<Item>, AspectList>> TAGS = new ArrayList<>();

	private AspectTable() {
	}

	static {
		// Earth and stone
		item(Items.DIRT, "T2");
		item(Items.COARSE_DIRT, "T2 P1");
		item(Items.GRASS_BLOCK, "T2 V1");
		item(Items.STONE, "T2");
		item(Items.COBBLESTONE, "T1 P1");
		item(Items.COBBLED_DEEPSLATE, "T2 P1");
		item(Items.DEEPSLATE, "T3");
		item(Items.ANDESITE, "T2");
		item(Items.DIORITE, "T2");
		item(Items.GRANITE, "T2");
		item(Items.TUFF, "T2");
		item(Items.GRAVEL, "T1 P2");
		item(Items.SAND, "T1 P1");
		item(Items.RED_SAND, "T1 P1");
		item(Items.CLAY_BALL, "T1 A1");
		item(Items.FLINT, "T1 P1");
		item(Items.NETHERRACK, "T1 I1");
		item(Items.OBSIDIAN, "T3 I2 O2");
		item(Items.GLASS, "T1 O1");
		// Water and air
		item(Items.KELP, "A1 V1");
		item(Items.ICE, "A2 O1");
		item(Items.PACKED_ICE, "A4 O2");
		item(Items.SNOWBALL, "A1 E1");
		item(Items.FEATHER, "E2");
		item(Items.STRING, "E1 V1");
		item(Items.PHANTOM_MEMBRANE, "E3 V2");
		item(Items.ENDER_PEARL, "E4 O2 P2");
		// Fire
		item(Items.COAL, "I2 T1");
		item(Items.CHARCOAL, "I2");
		item(Items.BLAZE_POWDER, "I3 P1");
		item(Items.BLAZE_ROD, "I6 P2");
		item(Items.MAGMA_CREAM, "I2 V1 A1");
		item(Items.GUNPOWDER, "I2 P2 E1");
		// Order
		item(Items.QUARTZ, "O3");
		item(Items.REDSTONE, "O1 I1");
		item(Items.LAPIS_LAZULI, "O2 A1");
		item(Items.AMETHYST_SHARD, "O2 T1");
		item(Items.GLOWSTONE_DUST, "I1 O1 E1");
		item(Items.DIAMOND, "O10 T6");
		item(Items.EMERALD, "O8 V4");
		// Life
		item(Items.ROTTEN_FLESH, "V1 P2");
		item(Items.BONE, "V2 T1");
		item(Items.BONE_MEAL, "V1");
		item(Items.SPIDER_EYE, "V1 P1");
		item(Items.WHEAT, "V2");
		item(Items.WHEAT_SEEDS, "V1");
		item(Items.CARROT, "V1");
		item(Items.POTATO, "V1");
		item(Items.BEETROOT, "V1");
		item(Items.APPLE, "V2");
		item(Items.SUGAR_CANE, "V1 A1");
		item(Items.MELON_SLICE, "V1 A1");
		item(Items.PUMPKIN, "V2 A1");
		item(Items.EGG, "V2");
		item(Items.BEEF, "V2");
		item(Items.PORKCHOP, "V2");
		item(Items.CHICKEN, "V2");
		item(Items.MUTTON, "V2");
		item(Items.COD, "V2 A1");
		item(Items.SALMON, "V2 A1");
		item(Items.LEATHER, "V2 T1");
		item(Items.SLIME_BALL, "V2 A2");
		// Metal
		item(Items.IRON_INGOT, "M4 T1");
		item(Items.RAW_IRON, "M4 T1 P1");
		item(Items.IRON_ORE, "M4 T2");
		item(Items.DEEPSLATE_IRON_ORE, "M4 T3");
		item(Items.COPPER_INGOT, "M3 T1");
		item(Items.RAW_COPPER, "M3 T1 P1");
		item(Items.COPPER_ORE, "M3 T2");
		item(Items.DEEPSLATE_COPPER_ORE, "M3 T3");
		item(Items.GOLD_INGOT, "M4 O2");
		item(Items.RAW_GOLD, "M4 O2 P1");
		item(Items.GOLD_ORE, "M4 O2 T2");
		item(Items.DEEPSLATE_GOLD_ORE, "M4 O2 T3");
		item(Items.NETHERITE_SCRAP, "M8 I4 P4");

		tag(ItemTags.LOGS, "V2 T1");
		tag(ItemTags.PLANKS, "V1");
		tag(ItemTags.SAPLINGS, "V2");
		tag(ItemTags.LEAVES, "V1");
		tag(ItemTags.FLOWERS, "V1");
		tag(ItemTags.WOOL, "V1 E1");
	}

	public static AspectList get(ItemStack stack) {
		if (stack.isEmpty()) {
			return AspectList.EMPTY;
		}
		AspectList exact = ITEMS.get(stack.getItem());
		if (exact != null) {
			return exact;
		}
		for (Map.Entry<TagKey<Item>, AspectList> entry : TAGS) {
			if (stack.is(entry.getKey())) {
				return entry.getValue();
			}
		}
		return AspectList.EMPTY;
	}

	public static boolean has(ItemStack stack) {
		return !get(stack).isEmpty();
	}

	/** Exact item entries, for balance checks. */
	public static Map<Item, AspectList> items() {
		return Collections.unmodifiableMap(ITEMS);
	}

	private static void item(Item item, String spec) {
		ITEMS.put(item, parse(spec));
	}

	private static void tag(TagKey<Item> tag, String spec) {
		TAGS.add(Map.entry(tag, parse(spec)));
	}

	static AspectList parse(String spec) {
		AspectList.Builder builder = AspectList.builder();
		for (String part : spec.trim().split("\\s+")) {
			Aspect aspect = switch (part.charAt(0)) {
				case 'T' -> Aspect.TERRA;
				case 'A' -> Aspect.AQUA;
				case 'I' -> Aspect.IGNIS;
				case 'E' -> Aspect.AER;
				case 'O' -> Aspect.ORDO;
				case 'P' -> Aspect.PERDITIO;
				case 'V' -> Aspect.VITA;
				case 'M' -> Aspect.METALLUM;
				default -> throw new IllegalArgumentException("Unknown aspect in " + spec);
			};
			builder.add(aspect, Integer.parseInt(part.substring(1)));
		}
		return builder.build();
	}

	/** Public parser used by recipes. */
	public static AspectList of(String spec) {
		return parse(spec);
	}
}
