#!/usr/bin/env bash
# Temporary development helper: prints public API signatures from the game and Fabric API jars.
set -u
JARS=$( (find ~/.gradle .gradle -name '*.jar' 2>/dev/null) | grep -E 'minecraft-(common|clientOnly|merged)[^/]*\.jar$' | grep -v sources | sort -u)
echo "== game jars"; echo "$JARS"
FAPI=$(find ~/.gradle -name 'fabric-*.jar' 2>/dev/null | grep -v sources | sort -u)
CP=$(echo $JARS $FAPI | tr ' ' ':')
echo "== class search"
for j in $JARS $FAPI; do unzip -Z1 "$j" 2>/dev/null; done | grep -E '\.class$' | grep -vE '\$[0-9]' | grep -E 'GuiGraphicsExtractor|CreativeModeTab|ItemTooltipCallback|ItemGroup|gametest/v1/GameTest|GameTestHelper\.class|MenuScreens|ValueInput\.class|ValueOutput\.class|BlockEntityTicker|SimpleContainerData|TransparentBlock|ItemCooldowns|TooltipDisplay\.class|DataComponents\.class|RenderPipelines\.class|ItemTags\.class|BlockTags\.class|ConventionalBlockTags|ExtendedScreenHandler|FabricLootTable' | sort -u
p() { # class [regex]
  echo "== $1"
  out=$(javap -cp "$CP" -protected "$1" 2>&1)
  if [ -n "${2:-}" ]; then echo "$out" | grep -E "class |interface |$2"; else echo "$out"; fi
}
p net.minecraft.world.level.block.state.BlockBehaviour 'use|getMenuProvider|codec|getRenderShape|affectNeighbors|getAnalog|hasAnalog|getShadeBrightness|propagatesSkylight|tick\('
p 'net.minecraft.world.level.block.state.BlockBehaviour$Properties' 'Properties [a-zA-Z]+\('
p net.minecraft.world.level.block.BaseEntityBlock
p net.minecraft.world.level.block.EntityBlock
p net.minecraft.world.level.block.Block 'createBlockStateDefinition|registerDefaultState|getStateForPlacement|getDrops|simpleCodec|Block\('
p net.minecraft.world.level.block.HorizontalDirectionalBlock
p net.minecraft.world.level.block.TransparentBlock
p net.minecraft.world.level.block.entity.BlockEntity
p net.minecraft.world.level.block.entity.BlockEntityType 'BlockEntityType\('
p net.minecraft.world.level.block.entity.BlockEntityTicker
p net.minecraft.world.level.block.state.properties.BlockStateProperties 'HORIZONTAL_FACING| LIT'
p net.minecraft.world.item.context.BlockPlaceContext 'Direction'
p net.minecraft.world.inventory.AbstractContainerMenu
p net.minecraft.world.inventory.Slot
p net.minecraft.world.inventory.ContainerData
p net.minecraft.world.inventory.SimpleContainerData
p net.minecraft.world.inventory.MenuType 'MenuType\('
p net.minecraft.world.Container
p net.minecraft.world.WorldlyContainer
p net.minecraft.world.Containers
p net.minecraft.world.ContainerHelper
p net.minecraft.world.MenuProvider
p net.minecraft.world.SimpleMenuProvider
p net.minecraft.world.level.storage.ValueInput
p net.minecraft.world.level.storage.ValueOutput
p net.minecraft.world.item.Item 'use|inventoryTick|appendHoverText|isFoil|Item\('
p 'net.minecraft.world.item.Item$Properties'
p net.minecraft.world.item.ItemStack 'hurtAndBreak|isEmpty|getCount|shrink|grow| copy|is\(|getItem\(|getMaxStackSize|set\(|get\(|isSameItemSameComponents|split|copyWithCount|consume|ItemStack\('
p net.minecraft.world.InteractionResult
p net.minecraft.world.entity.player.Player 'getCooldowns|displayClientMessage|getInventory|sendSystemMessage|addItem|openMenu|isCreative'
p net.minecraft.world.item.ItemCooldowns
p net.minecraft.world.entity.Entity ' isShiftKeyDown|position\(|blockPosition\(|getBoundingBox\(|setDeltaMovement|getDeltaMovement|level\(|getEyePosition|distanceToSqr\(net'
p net.minecraft.world.entity.item.ItemEntity 'public'
p net.minecraft.world.level.Level 'getEntitiesOfClass|destroyBlock|isClientSide|playSound\(|getGameTime|addFreshEntity|sendBlockUpdated|updateNeighbourForOutputSignal'
p net.minecraft.server.level.ServerLevel 'sendParticles'
p net.minecraft.core.BlockPos 'betweenClosed\(|offset\(int|relative\(|MutableBlockPos'
p net.minecraft.core.Direction 'getClockWise|getCounterClockWise|getOpposite|getStepX|getStepZ'
p net.minecraft.client.gui.GuiGraphicsExtractor
p net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
p net.minecraft.client.gui.screens.Screen 'extract|addRenderableWidget|init\(|font'
p net.minecraft.client.gui.screens.MenuScreens 'register'
p net.minecraft.network.chat.Component 'translatable\(|literal\('
p net.minecraft.gametest.framework.GameTestHelper 'setBlock|getBlockEntity|succeedWhen|assertTrue|absolutePos|getLevel|succeed\(|fail\(|makeMockPlayer|spawnItem|runAfterDelay|assertValueEqual|assertBlockPresent|useBlock|getEntities|assertItemEntityPresent|assertFalse'
p net.minecraft.world.item.TooltipFlag
p net.minecraft.world.item.component.TooltipDisplay ''
p net.minecraft.core.component.DataComponents 'ENCHANTMENT_GLINT_OVERRIDE|CUSTOM_DATA|MAX_DAMAGE| DAMAGE'
p net.minecraft.tags.TagKey 'create'
p net.minecraft.world.item.CreativeModeTab 'builder|Output|accept'
p 'net.minecraft.world.item.CreativeModeTab$Builder'
p net.minecraft.world.item.Items 'EXPERIENCE_BOTTLE|IRON_INGOT |GLASS_BOTTLE|COPPER_INGOT |AMETHYST_SHARD|LEATHER |SLIME_BALL|ROTTEN_FLESH| GLOWSTONE_DUST|WOOL|DYE|LOG|PLANKS'
p net.minecraft.world.level.block.Blocks 'MAGMA_BLOCK|STONE_BRICKS|GLASS '
p net.minecraft.sounds.SoundEvents 'BREWING_STAND_BREW|ENCHANTMENT_TABLE_USE|AMETHYST_BLOCK_CHIME|EXPERIENCE_ORB_PICKUP|ITEM_PICKUP|BEACON_ACTIVATE'
p net.minecraft.core.particles.ParticleTypes ' WITCH| ENCHANT| END_ROD| PORTAL| FLAME'
p net.minecraft.world.level.block.SoundType ' METAL| STONE| GLASS| DEEPSLATE_BRICKS| AMETHYST'
p net.minecraft.world.entity.player.Inventory 'placeItemBackInInventory|add\('
p net.minecraft.world.inventory.ContainerLevelAccess
p net.minecraft.client.renderer.RenderPipelines 'GUI_TEXTURED'
echo "== fabric"
for c in $(for j in $FAPI; do unzip -Z1 "$j" 2>/dev/null; done | grep -E '(ItemTooltipCallback|FabricCreativeModeTab|FabricItemGroup|gametest/v1/GameTest|ConventionalBlockTags|ConventionalItemTags)\.class$' | sed 's/\.class$//; s|/|.|g' | sort -u); do
  if echo "$c" | grep -q Conventional; then p "$c" ' ORES|RAW_MATERIALS|INGOTS'; else p "$c"; fi
done
