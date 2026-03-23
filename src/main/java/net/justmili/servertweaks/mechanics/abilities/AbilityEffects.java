package net.justmili.servertweaks.mechanics.abilities;

import net.justmili.servertweaks.mechanics.abilities.sets.AbilityModifiers;
import net.justmili.servertweaks.mechanics.abilities.sets.FoodCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Set;
import java.util.UUID;

public final class AbilityEffects {

    public static void onWorldTick(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            Set<Ability> abilities = AbilityManager.getAbilities(uuid);
            if (abilities.isEmpty()) continue;
            for (Ability ability : abilities) {
                if (ability instanceof TickingAbility ticking) {
                    ticking.tick(player, level);
                }
            }
        }
    }

    public static boolean onAllowDamage(ServerPlayer player, DamageSource source, float amount) {
        Set<Ability> abilities = AbilityManager.getAbilities(player.getUUID());

        if (abilities.contains(AbilityRegistry.FIRE_IMMUNE)) {
            if (source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.HOT_FLOOR)) return false;
        }
        if (abilities.contains(AbilityRegistry.FALL_IMMUNE)) {
            if (source.is(DamageTypes.FALL)) return false;
        }
        if (abilities.contains(AbilityRegistry.BREATHES_UNDERWATER)) {
            if (source.is(DamageTypes.DROWN)) return false;
        }
        if (abilities.contains(AbilityRegistry.FREEZE_IMMUNE)) {
            if (source.is(DamageTypes.FREEZE)) return false;
        }

        return true;
    }

    public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

        if (!AbilityManager.has(sp.getUUID(), AbilityRegistry.CAN_FEED_FROM_GRASS)) return InteractionResult.PASS;

        BlockPos pos = hit.getBlockPos();
        if (!level.getBlockState(pos).is(Blocks.SHORT_GRASS)) return InteractionResult.PASS;

        level.destroyBlock(pos, false);
        FoodData food = sp.getFoodData();
        food.eat(1, 0.1F);
        player.swing(InteractionHand.MAIN_HAND, true);

        return InteractionResult.SUCCESS;
    }

    // Returns false to cancel eating
    public static boolean onItemUse(ServerPlayer player, ItemStack stack) {
        if (!stack.has(DataComponents.FOOD)) return true;
        Set<Ability> abilities = AbilityManager.getAbilities(player.getUUID());
        Set<AbilityModifiers> modifiers = AbilityManager.getModifiers(player.getUUID());
        return FoodCategories.canEat(abilities, modifiers, stack.getItem());
    }

    // Returns true if the player should be climbing right now
    public static boolean shouldClimb(ServerPlayer player) {
        if (!AbilityManager.has(player.getUUID(), AbilityRegistry.CLIMBS_WALLS)) return false;
        if (player.onGround()) return false;
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).isSolid()) return true;
        }
        return false;
    }

    // Called on join and periodically for STRONG players
    public static void updateStrongHealth(ServerPlayer player) {
        if (!AbilityManager.has(player.getUUID(), AbilityRegistry.STRONG)) return;
        int armor = player.getArmorValue();
        float maxHp = Math.max(40.0F, Math.min(100.0F, 100.0F - (armor * 3.0F)));
        player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
        if (player.getHealth() > maxHp) player.setHealth(maxHp);
    }
}