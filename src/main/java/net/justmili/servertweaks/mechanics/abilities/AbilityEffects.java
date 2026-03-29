package net.justmili.servertweaks.mechanics.abilities;

import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.justmili.servertweaks.mechanics.abilities.ability.DietCategories;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilitiesRegistry;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilityModifierRegistry;
import net.justmili.servertweaks.mechanics.abilities.registry.TickingAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.UUID;

public class AbilityEffects {
    public static void registerAbilityEvents() {
        if (!(Config.playerAbilities.get())) return;
        TickEvent.PLAYER_POST.register(AbilityEffects::tickTickingAbilities);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(AbilityEffects::specialDamageImmune);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(AbilityEffects::grassEater);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(AbilityEffects::dietRestrictions);
        InteractionEvent.RIGHT_CLICK_ITEM.register(AbilityEffects::dietRestrictions);
    }

    private static void tickTickingAbilities(Player ticking) {
        if (!(ticking instanceof ServerPlayer player)) return;
        ServerLevel level = player.level();
        UUID uuid = player.getUUID();
        Set<Ability> abilities = AbilityManager.getAbilities(uuid);

        for (Ability ability : abilities) {
            if (ability instanceof TickingAbility tickingAbility) {
                tickingAbility.tick(player, level);
            }
        }
    }

    private static boolean specialDamageImmune(LivingEntity entity, DamageSource source, float value) {
        if (!(entity instanceof ServerPlayer player)) return true;
        Set<Ability> abilities = AbilityManager.getAbilities(player.getUUID());

        if (abilities.contains(AbilitiesRegistry.FIRE_IMMUNE) && (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)
            || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR))) return false;
        if (abilities.contains(AbilitiesRegistry.FREEZE_IMMUNE) && source.is(DamageTypes.FREEZE)) return false;
        if (abilities.contains(AbilitiesRegistry.FALL_IMMUNE) && source.is(DamageTypes.FALL)) return false;
        if (abilities.contains(AbilitiesRegistry.BREATHES_UNDERWATER) && source.is(DamageTypes.DROWN)) return false;

        return true;
    }

    private static InteractionResult grassEater(Player interacting, InteractionHand hand, BlockPos pos, Direction direction) { // Block RC
        if (interacting.level().isClientSide()) return InteractionResult.PASS;
        if (!(interacting instanceof ServerPlayer player)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.GRASS_EATER)) return InteractionResult.PASS;
        if (!DietCategories.GRASSY.contains(player.level().getBlockState(pos).getBlock())) return InteractionResult.PASS;

        player.level().destroyBlock(pos, false);
        FoodData food = player.getFoodData();
        food.eat(2, 0.2F);
        player.swing(InteractionHand.MAIN_HAND, true);

        return InteractionResult.SUCCESS;
    }

    public static boolean shouldClimb(ServerPlayer player) {
        if (!(Config.playerAbilities.get())) return false;
        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.CLIMBS_WALLS)) return false;
        if (player.onGround()) return false;
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(dir);
            if (level.getBlockState(side).isSolid() || level.getBlockState(side.above()).isSolid()) return true;
        }
        return false;
    }

    private static InteractionResult dietRestrictions(Player interacting, InteractionHand hand) {
        if (interacting.level().isClientSide()) return InteractionResult.PASS;
        if (!(interacting instanceof ServerPlayer player)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (isDietBlocked(player, player.getItemInHand(hand))) return InteractionResult.FAIL;

        return InteractionResult.PASS;
    }
    private static InteractionResult dietRestrictions(Player interacting, InteractionHand hand, BlockPos pos, Direction direction) {
        if (interacting.level().isClientSide()) return InteractionResult.PASS;
        if (!(interacting instanceof ServerPlayer player)) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;

        if (isDietBlocked(player, player.getItemInHand(hand))) return InteractionResult.FAIL;

        return InteractionResult.PASS;
    }
    private static boolean isDietBlocked(ServerPlayer player, ItemStack stack) {
        if (!stack.has(DataComponents.FOOD)) return false;
        UUID uuid = player.getUUID();
        Set<Ability> abilities = AbilityManager.getAbilities(uuid);
        boolean hasGold = AbilityManager.has(uuid, AbilityModifierRegistry.ADD_GOLD_FOODS_TO_DIET);

        if (abilities.contains(AbilitiesRegistry.CARNIVORE)) {
            if (hasGold && DietCategories.GOLDEN_FOODS.contains(stack.getItem())) return false;
            return !DietCategories.CARNIVORE.contains(stack.getItem());
        }
        if (abilities.contains(AbilitiesRegistry.VEGETARIAN)) {
            if (hasGold && DietCategories.GOLDEN_FOODS.contains(stack.getItem())) return false;
            return !DietCategories.VEGETARIAN.contains(stack.getItem());
        }
        if (abilities.contains(AbilitiesRegistry.ONLY_EATS_SWEETS)) {
            if (hasGold && DietCategories.GOLDEN_FOODS.contains(stack.getItem())) return false;
            return !DietCategories.SWEET.contains(stack.getItem());
        }
        return false;
    }
}
