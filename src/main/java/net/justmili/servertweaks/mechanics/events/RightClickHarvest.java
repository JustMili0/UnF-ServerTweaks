package net.justmili.servertweaks.mechanics.events;

import dev.architectury.event.EventResult;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mixin.accessors.CropBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class RightClickHarvest {

    public static InteractionResult onUseBlock(Player player, InteractionHand hand, BlockPos pos, Direction direction) {
        Level level = player.level();
        if (!Config.rightClickHarvest.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (player.isSpectator()) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        boolean hoeHeld = player.getMainHandItem().getItem() instanceof HoeItem;

        switch (block) {
            case CropBlock cropBlock -> {
                if (! harvestCrop(player, (ServerLevel) level, pos, state, cropBlock)) return InteractionResult.PASS;
                if (hoeHeld) {
                    for (BlockPos near : BlockPos.betweenClosed(pos.offset(- 1, 0, - 1), pos.offset(1, 0, 1))) {
                        if (near.equals(pos)) continue;
                        BlockState nearState = level.getBlockState(near);
                        if (nearState.getBlock() instanceof CropBlock nearCrop)
                            harvestCrop(player, (ServerLevel) level, near.immutable(), nearState, nearCrop);
                    }
                }
            }
            case NetherWartBlock netherWartBlock -> {
                if (! harvestNetherWart(player, (ServerLevel) level, pos, state)) return InteractionResult.PASS;
                if (hoeHeld) {
                    for (BlockPos near : BlockPos.betweenClosed(pos.offset(- 1, 0, - 1), pos.offset(1, 0, 1))) {
                        if (near.equals(pos)) continue;
                        BlockState nearState = level.getBlockState(near);
                        if (nearState.getBlock() instanceof NetherWartBlock)
                            harvestNetherWart(player, (ServerLevel) level, near.immutable(), nearState);
                    }
                }
            }
            case CocoaBlock cocoaBlock -> {
                if (! harvestCocoa(player, (ServerLevel) level, pos, state)) return InteractionResult.PASS;
            }
            case SugarCaneBlock sugarCaneBlock -> {
                return harvestSugarCane(player, (ServerLevel) level, pos);
            }
            default -> {
                return InteractionResult.PASS;
            }
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        damageHoeIfHeld(player, player.getMainHandItem(), (ServerLevel) level);
        return InteractionResult.SUCCESS;
    }

    private static boolean harvestCrop(Player player, ServerLevel level, BlockPos pos, BlockState state, CropBlock cropBlock) {
        if (!cropBlock.isMaxAge(state)) return false;

        ItemStack tool = player.getMainHandItem();
        List<ItemStack> drops = getDrops(level, pos, state, player, tool);
        removeOneSeed(drops, new ItemStack(cropBlock.asItem()));

        IntegerProperty ageProp = ((CropBlockAccessor) cropBlock).invokeGetAgeProperty();
        level.setBlock(pos, state.setValue(ageProp, 0), Block.UPDATE_ALL);
        for (ItemStack drop : drops) Block.popResource(level, pos, drop);

        return true;
    }

    private static boolean harvestNetherWart(Player player, ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(NetherWartBlock.AGE) < NetherWartBlock.MAX_AGE) return false;

        ItemStack tool = player.getMainHandItem();
        List<ItemStack> drops = getDrops(level, pos, state, player, tool);
        removeOneSeed(drops, new ItemStack(Items.NETHER_WART));

        level.setBlock(pos, state.setValue(NetherWartBlock.AGE, 0), Block.UPDATE_ALL);
        for (ItemStack drop : drops) Block.popResource(level, pos, drop);

        return true;
    }

    private static boolean harvestCocoa(Player player, ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getValue(CocoaBlock.AGE) < 2) return false;

        ItemStack tool = player.getMainHandItem();
        List<ItemStack> drops = getDrops(level, pos, state, player, tool);
        removeOneSeed(drops, new ItemStack(Items.COCOA_BEANS));

        level.setBlock(pos, state.setValue(CocoaBlock.AGE, 0), Block.UPDATE_ALL);
        for (ItemStack drop : drops) Block.popResource(level, pos, drop);

        return true;
    }

    private static InteractionResult harvestSugarCane(Player player, ServerLevel level, BlockPos clickedPos) {
        // Walk down to find the bottom sugar cane block
        BlockPos bottom = clickedPos;
        while (level.getBlockState(bottom.below()).is(Blocks.SUGAR_CANE)) bottom = bottom.below();

        BlockPos breakFrom = bottom.above();
        if (!level.getBlockState(breakFrom).is(Blocks.SUGAR_CANE)) return InteractionResult.PASS;

        ItemStack tool = player.getMainHandItem();
        BlockPos current = breakFrom;
        while (level.getBlockState(current).is(Blocks.SUGAR_CANE)) {
            List<ItemStack> drops = getDrops(level, current, level.getBlockState(current), player, tool);
            level.removeBlock(current, false);
            for (ItemStack drop : drops) Block.popResource(level, current, drop);
            current = current.above();
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        return InteractionResult.SUCCESS;
    }

    // Builds full LootParams so Fortune, Silk Touch, and all loot table conditions apply correctly
    private static List<ItemStack> getDrops(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        LootParams.Builder builder = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.BLOCK_STATE, state)
            .withParameter(LootContextParams.TOOL, tool)
            .withOptionalParameter(LootContextParams.THIS_ENTITY, player);
        return state.getDrops(builder);
    }

    // Removes one seed from drops so the crop effectively replants itself
    private static void removeOneSeed(List<ItemStack> drops, ItemStack seedItem) {
        for (ItemStack drop : drops) {
            if (ItemStack.isSameItem(drop, seedItem) && drop.getCount() > 0) {
                drop.shrink(1);
                return;
            }
        }
    }

    // Damages hoe by 1 durability if the player is holding one (respects Unbreaking)
    private static void damageHoeIfHeld(Player player, ItemStack stack, ServerLevel level) {
        if (stack.getItem() instanceof HoeItem) {
            stack.hurtAndBreak(1, level, null, item -> {});
        }
    }
}