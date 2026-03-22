package net.justmili.servertweaks.mechanics.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AbilityEffects {

    public static void onWorldTick(ServerLevel level) {
        for (ServerPlayer player : level.players()) {
            UUID uuid = player.getUUID();
            Set<Ability> abilities = AbilityManager.getAbilities(uuid);
            if (abilities.isEmpty()) continue;

            tickBurnsInDaylight(player, level, abilities);
            tickNightVision(player, abilities);
            tickJumpBoost(player, abilities);
            tickSpeedInWater(player, abilities);
            tickScareCreepers(player, level, abilities);
            tickScarePhantoms(player, level, abilities);
            tickFearedByPiglins(player, level, abilities);
            tickHuntedByFox(player, level, abilities);
            tickHuntedByWolf(player, level, abilities);
            tickAttractsBees(player, level, abilities);
            tickHatesWater(player, level, abilities);
            tickStrong(player, abilities);
        }
    }

    public static boolean onAllowDamage(ServerPlayer player, DamageSource source, float amount) {
        Set<Ability> abilities = AbilityManager.getAbilities(player.getUUID());

        if (abilities.contains(Ability.FIRE_IMMUNE)) {
            if (source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.HOT_FLOOR)) {
                return false;
            }
        }

        if (abilities.contains(Ability.FALL_IMMUNE)) {
            if (source.is(DamageTypes.FALL)) {
                return false;
            }
        }

        if (abilities.contains(Ability.BREATHES_UNDERWATER)) {
            if (source.is(DamageTypes.DROWN)) {
                return false;
            }
        }

        return true;
    }

    public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.PASS;
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

        Set<Ability> abilities = AbilityManager.getAbilities(sp.getUUID());
        if (!abilities.contains(Ability.CAN_FEED_FROM_GRASS)) return InteractionResult.PASS;

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
        return FoodCategories.canEat(abilities, stack.getItem());
    }

    // Returns true if the player should be climbing right now
    public static boolean shouldClimb(ServerPlayer player) {
        if (!AbilityManager.has(player.getUUID(), Ability.CLIMBS_WALLS)) return false;
        if (player.onGround()) return false;
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).isSolid()) return true;
        }
        return false;
    }

    public static void updateStrongHealth(ServerPlayer player) {
        if (!AbilityManager.has(player.getUUID(), Ability.STRONG)) return;

        int armor = player.getArmorValue(); // 0 (naked) to 20 (full netherite)
        // Linear scale: 0 armor = 100HP, 20 armor = 40HP
        float maxHp = Math.max(40.0F, Math.min(100.0F, 100.0F - (armor * 3.0F)));

        player.getAttribute(Attributes.MAX_HEALTH)
            .setBaseValue(maxHp);
        if (player.getHealth() > maxHp) player.setHealth(maxHp);
    }

    private static void tickBurnsInDaylight(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.BURNS_IN_DAYLIGHT)) return;
        if (!level.isBrightOutside() || !level.canSeeSky(player.blockPosition())) return;

        int skyLight = level.getBrightness(LightLayer.SKY, player.blockPosition());
        if (skyLight <= 8) return;

        // HEAD slot = helmet
        boolean hasHelmet = !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        if (hasHelmet) return;

        boolean inWater = player.isInWater();
        if (inWater) {
            if (level.getGameTime() % 30 == 0) {
                player.hurt(level.damageSources().inFire(), 0.5F);
            }
        } else {
            player.setSharedFlagOnFire(true);
            player.setRemainingFireTicks(8);
        }
    }

    private static void tickNightVision(ServerPlayer player, Set<Ability> abilities) {
        if (!abilities.contains(Ability.NIGHT_VISION)) return;
        if (player.level().getGameTime() % 100 == 0) {
            player.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }

    private static void tickJumpBoost(ServerPlayer player, Set<Ability> abilities) {
        if (!abilities.contains(Ability.JUMP_BOOST)) return;
        if (player.level().getGameTime() % 100 == 0) {
            player.addEffect(new MobEffectInstance(
                MobEffects.JUMP_BOOST, 300, 0, false, false));
        }
    }

    private static void tickSpeedInWater(ServerPlayer player, Set<Ability> abilities) {
        if (!abilities.contains(Ability.SPEED_IN_WATER)) return;
        if (!player.isInWater()) return;
        if (player.level().getGameTime() % 100 == 0) {
            player.addEffect(new MobEffectInstance(
                MobEffects.DOLPHINS_GRACE, 300, 0, false, false));
        }
    }

    private static void tickScareCreepers(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.SCARES_CREEPERS)) return;
        List<Creeper> nearby = level.getEntitiesOfClass(Creeper.class, player.getBoundingBox().inflate(6.0));
        for (Creeper creeper : nearby) {
            creeper.setTarget(null);
            creeper.getNavigation().moveTo(
                creeper.getX() + (creeper.getX() - player.getX()),
                creeper.getY(),
                creeper.getZ() + (creeper.getZ() - player.getZ()), 1.2);
        }
    }

    private static void tickScarePhantoms(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.SCARES_PHANTOMS)) return;
        List<Phantom> nearby = level.getEntitiesOfClass(Phantom.class, player.getBoundingBox().inflate(16.0));
        for (Phantom phantom : nearby) {
            phantom.setTarget(null);
            phantom.getNavigation().moveTo(
                phantom.getX() + (phantom.getX() - player.getX()),
                phantom.getY() + 8,
                phantom.getZ() + (phantom.getZ() - player.getZ()), 1.2);
        }
    }

    private static void tickFearedByPiglins(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.FEARED_BY_PIGLINS)) return;
        List<Piglin> nearby = level.getEntitiesOfClass(Piglin.class, player.getBoundingBox().inflate(8.0));
        for (Piglin piglin : nearby) {
            if (!piglin.isAdult()) continue;
            piglin.setTarget(null);
            piglin.getNavigation().moveTo(
                piglin.getX() + (piglin.getX() - player.getX()),
                piglin.getY(),
                piglin.getZ() + (piglin.getZ() - player.getZ()), 1.1);
        }
    }

    private static void tickHuntedByFox(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.HUNTED_BY_FOX)) return;
        List<Fox> nearby = level.getEntitiesOfClass(Fox.class, player.getBoundingBox().inflate(16.0));
        for (Fox fox : nearby) {
            if (fox.getTarget() != null) continue;
            fox.setTarget(player);
        }
    }

    private static void tickHuntedByWolf(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.HUNTED_BY_WOLF)) return;
        List<Wolf> nearby = level.getEntitiesOfClass(Wolf.class, player.getBoundingBox().inflate(16.0));
        for (Wolf wolf : nearby) {
            if (wolf.isTame()) continue;
            if (wolf.getTarget() == null) wolf.setTarget(player);
        }
    }

    private static void tickAttractsBees(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.ATTRACTS_BEES)) return;
        List<Bee> nearby = level.getEntitiesOfClass(Bee.class, player.getBoundingBox().inflate(10.0));
        for (Bee bee : nearby) {
            if (bee.getTarget() != null) continue;
            if (bee.getNavigation().isDone()) bee.getNavigation().moveTo(player, 0.8);
        }
    }

    private static void tickHatesWater(ServerPlayer player, ServerLevel level, Set<Ability> abilities) {
        if (!abilities.contains(Ability.HATES_WATER)) return;
        boolean inWaterOrRain = player.isInWater()
            || (level.isRaining() && level.canSeeSky(player.blockPosition()));
        if (inWaterOrRain && level.getGameTime() % 20 == 0) {
            player.hurt(level.damageSources().drown(), 1.0F);
        }
    }

    private static void tickStrong(ServerPlayer player, Set<Ability> abilities) {
        if (!abilities.contains(Ability.STRONG)) return;
        if (player.level().getGameTime() % 10 == 0) updateStrongHealth(player);
    }
}