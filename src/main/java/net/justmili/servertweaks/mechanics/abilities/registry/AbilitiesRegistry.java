package net.justmili.servertweaks.mechanics.abilities.registry;

import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.justmili.servertweaks.mixin.accessors.FoxAccessor;
import net.justmili.servertweaks.util.ScalerUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilitiesRegistry {
    // Extra Ability variables
    private static final Identifier SLOW_SPEED = ServerTweaks.asResource("slow_speed");
    private static final Identifier STRONG_HP = ServerTweaks.asResource("strong_health");
    private static final Identifier STRONG_DAMAGE = ServerTweaks.asResource("strong_damage");

    // Registry
    private static final Map<String, Ability> REGISTRY = new HashMap<>();

    public static final Ability BURNS_IN_DAYLIGHT = register(new BurnsInDaylight());                // FINISHED
    public static final Ability FIRE_IMMUNE = register(new Ability("FIRE_IMMUNE"));           // FINISHED
    public static final Ability FREEZE_IMMUNE = register(new Ability("FREEZE_IMMUNE"));       // FINISHED
    public static final Ability FALL_IMMUNE = register(new Ability("FALL_IMMUNE"));           // FINISHED
    public static final Ability CLIMBS_WALLS = register(new Ability("CLIMBS_WALLS"));         // FINISHED (UNTESTED)
    public static final Ability AQUA_GRACE = register(new AquaGrace());                             // FINISHED
    public static final Ability LIGHT = register(new Light());                                      // FINISHED (UNTESTED)
    public static final Ability SWIFT = register(new Swift());                                      // FINISHED
    public static final Ability SLOW = register(new Slow());                                        // FINISHED (UNTESTED)
    public static final Ability HOPPY = register(new Hoppy());                                      // FINISHED
    public static final Ability DWARF = register(new Dwarf());                                      // FINISHED
    public static final Ability TOUGH = register(new Ability("TOUGH"));                       // FINISHED (UNTESTED)
    public static final Ability STRONG = register(new Strong());                                    // FINISHED (UNTESTED)
    public static final Ability WEAK_TO_DAMAGE = register(new Ability("WEAK_TO_DAMAGE"));     // FINISHED (UNTESTED)
    public static final Ability BREATHES_UNDERWATER = register(new BreathesUnderwater());           // FINISHED
    public static final Ability CANT_BREATHE_AIR = register(new Ability("CANT_BREATHE_AIR")); // WIP (Missing: all logic)
    public static final Ability CANT_SWIM = register(new Ability("CANT_SWIM"));               // WIP (Missing: all logic)
    public static final Ability HYDROPHOBIC = register(new Hydrophobic());                          // FINISHED (UNTESTED)
    public static final Ability NIGHT_VISION = register(new NightVision());                         // FINISHED
    public static final Ability HUNTED_BY_FOX = register(new HuntedByFox());                        // FINISHED (UNTESTED)
    public static final Ability HUNTED_BY_WOLF = register(new HuntedByWolf());                      // FINISHED (UNTESTED)
    public static final Ability SCARES_CREEPERS = register(new ScaresCreepers());                   // FINISHED
    public static final Ability SCARES_PHANTOMS = register(new ScaresPhantoms());                   // FINISHED
    public static final Ability FRIENDS_WITH_NATURE = register(new FriendsWithNature());            // FINISHED (UNTESTED)
    public static final Ability IS_MONSTER = register(new IsMonster());                             // FINISHED (UNTESTED)
    public static final Ability CARNIVORE = register(new Ability("CARNIVORE"));               // FINISHED (UNTESTED)
    public static final Ability VEGETARIAN = register(new Ability("VEGETARIAN"));             // FINISHED (UNTESTED)
    public static final Ability ONLY_EATS_SWEETS = register(new Ability("ONLY_EATS_SWEETS")); // FINISHED (UNTESTED)
    public static final Ability GRASS_EATER = register(new Ability("GRASS_EATER"));           // FINISHED (UNTESTED)

    private static Ability register(Ability ability) {
        REGISTRY.put(ability.getName(), ability);
        return ability;
    }
    public static @Nullable Ability byName(String name) {
        return REGISTRY.get(name);
    }

    // Define ticking abilities
    static class BurnsInDaylight extends TickingAbility {
        BurnsInDaylight() { super("BURNS_IN_DAYLIGHT"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (!level.isBrightOutside() || !level.canSeeSky(player.blockPosition())) return;

            if ((level.getBrightness(LightLayer.SKY, player.blockPosition()) <= 8)) return;
            boolean hasHelmet = !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
            if (hasHelmet) return;
            if (player.isInWater()) {
                if (level.getGameTime() % 30 == 0) player.hurt(level.damageSources().inFire(), 0.5F);
            } else {
                player.igniteForSeconds(2);
            }
        }
    }

    // FIRE_IMMUNE - Ticking in AbilityEffects
    // FREEZE_IMMUNE - Ticking in AbilityEffects
    // CLIMBS_WALLS - Boolean in AbilityEffects + PlayerMixin

    static class AquaGrace extends TickingAbility {
        AquaGrace() { super("AQUA_GRACE"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            applyEffect(player, MobEffects.CONDUIT_POWER);
        }
    }

    static class Light extends TickingAbility {
        Light() { super("LIGHT"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (player.getDeltaMovement().y < -0.4) applyEffect(player, MobEffects.SLOW_FALLING);
        }
    }

    static class Swift extends TickingAbility {
        Swift() { super("SWIFT"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (player.isSprinting()) applyEffect(player, MobEffects.SPEED, 30, 0);
        }
    }

    static class Slow extends TickingAbility {
        Slow() { super("SLOW"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);

            if (speed.getModifier(SLOW_SPEED) == null) {
                speed.addTransientModifier(new AttributeModifier(SLOW_SPEED, -0.47, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            }
        }
    }

    static class Hoppy extends TickingAbility {
        Hoppy() { super("HOPPY"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            applyEffect(player, MobEffects.JUMP_BOOST);
        }
    }

    static class Dwarf extends TickingAbility {
        Dwarf() { super("DWARF"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            AttributeInstance scale = ScalerUtil.getScale(player);
            if (scale != null && scale.getBaseValue() > 0.75) ScalerUtil.setScale(player, 0.75);
            applyEffect(player, MobEffects.HASTE, 2);
        }
    }

    // TOUGH - LivingEntityMixin

    static class Strong extends TickingAbility {
        Strong() { super("STRONG"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (level.getGameTime() % 5 != 0) return;

            int armor = player.getArmorValue();
            float targetHp = Math.max(40.0F, Math.min(100.0F, 100.0F - (armor * 3.0F)));
            AttributeInstance maxHp = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHp != null) {
                maxHp.removeModifier(STRONG_HP);
                maxHp.addTransientModifier(new AttributeModifier(STRONG_HP, targetHp - 20.0, AttributeModifier.Operation.ADD_VALUE));
                if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            }
            AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attack != null && attack.getModifier(STRONG_DAMAGE) == null) {
                attack.addTransientModifier(new AttributeModifier(STRONG_DAMAGE, 3.0, AttributeModifier.Operation.ADD_VALUE));
            }
        }
    }

    // WEAK_TO_DAMAGE - LivingEntityMixin

    static class BreathesUnderwater extends TickingAbility {
        BreathesUnderwater() { super("BREATHES_UNDERWATER"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (player.isInWater()) applyEffect(player, MobEffects.WATER_BREATHING, 30, 0);
        }
    }

    static class CantBreatheAir extends TickingAbility {
        CantBreatheAir() { super("CANT_BREATHE_AIR"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {

        }
    }

    static class CantSwim extends TickingAbility {
        CantSwim() { super("CANT_SWIM"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {

        }
    }

    static class Hydrophobic extends TickingAbility {
        Hydrophobic() { super("HYDROPHOBIC"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            boolean inWaterOrRain = player.isInWater()
                || (level.isRaining() && level.canSeeSky(player.blockPosition()))
                || level.getBlockState(player.blockPosition()).is(Blocks.WATER_CAULDRON);
            if (inWaterOrRain && level.getGameTime() % 20 == 0) player.hurt(level.damageSources().drown(), 1.0F);
        }
    }

    static class NightVision extends TickingAbility {
        NightVision() { super("NIGHT_VISION"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            if (level.isDarkOutside()) applyEffect(player, MobEffects.NIGHT_VISION);
        }
    }

    static class HuntedByFox extends TickingAbility {
        HuntedByFox() { super("HUNTED_BY_FOX"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            for (Fox fox : getNearby(player, Fox.class, 12.0)) {
                if (((FoxAccessor) fox).invokeTrusts(player)) continue;
                if (fox.getTarget() == null) fox.setTarget(player);
            }
        }
    }

    static class HuntedByWolf extends TickingAbility {
        HuntedByWolf() { super("HUNTED_BY_WOLF"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            for (Wolf wolf : getNearby(player, Wolf.class, 16.0)) {
                if (wolf.isTame()) continue;
                if (wolf.getTarget() == null) wolf.setTarget(player);
            }
        }
    }

    static class ScaresCreepers extends TickingAbility {
        ScaresCreepers() { super("SCARES_CREEPERS"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            for (Creeper creeper : getNearby(player, Creeper.class, 8.0)) {
                creeper.setTarget(null);
                creeper.getNavigation().moveTo(
                    creeper.getX() + (creeper.getX() - player.getX()),
                    creeper.getY(),
                    creeper.getZ() + (creeper.getZ() - player.getZ()), 1.2);
            }
        }
    }

    static class ScaresPhantoms extends TickingAbility {
        ScaresPhantoms() { super("SCARES_PHANTOMS"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            for (Phantom phantom : getNearby(player, Phantom.class, 16.0)) {
                phantom.setTarget(null);
                phantom.getNavigation().moveTo(
                    phantom.getX() + (phantom.getX() - player.getX()),
                    phantom.getY() + 8,
                    phantom.getZ() + (phantom.getZ() - player.getZ()), 1.2);
            }
        }
    }

    static class FriendsWithNature extends TickingAbility {
        FriendsWithNature() { super("FRIENDS_WITH_NATURE"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            for (Fox fox : getNearby(player, Fox.class, 24.0)) {
                FoxAccessor accessor = (FoxAccessor) fox;
                if (accessor.invokeTrusts(player)) continue;
                accessor.invokeAddTrustedEntity(player);
            }
            for (Wolf wolf : getNearby(player, Wolf.class, 24.0)) {
                if (!wolf.isTame() && wolf.getTarget() == player) wolf.setTarget(null);
            }

            // +Taming mixins
        }
    }

    static class IsMonster extends TickingAbility {
        IsMonster() { super("IS_MONSTER"); }

        @Override public void tick(ServerPlayer player, ServerLevel level) {
            for (Villager villager : getNearby(player, Villager.class, 16.0)) {
                villager.getNavigation().moveTo(
                    villager.getX() + (villager.getX() - player.getX()),
                    villager.getY(),
                    villager.getZ() + (villager.getZ() - player.getZ()), 1.2);
            }
            for (IronGolem ironGolem : getNearby(player, IronGolem.class, 16.0)) {
                if (ironGolem.getTarget() != player) ironGolem.setTarget(player);
            }
            for (SnowGolem snowGolem : getNearby(player, SnowGolem.class, 24.0)) {
                if (snowGolem.getTarget() != player) snowGolem.setTarget(player);
            }
            for (Pillager pillager : getNearby(player, Pillager.class, 64.0)) {
                if (pillager.getTarget() == player) pillager.setTarget(null);
            }
        }
    }

    // CARNIVORE - AbilityEffects         (NOT MADE)
    // VEGETARIAN - AbilityEffects        (NOT MADE)
    // ONLY_EATS_SWEETS - AbilityEffects  (NOT MADE)
    // GRASS_EATER - AbilityEffects       (NOT MADE)

    // Ticking abilities helper methods
    private static void applyEffect(ServerPlayer player, Holder<@NotNull MobEffect> effects, int duration, int power) {
        player.addEffect(new MobEffectInstance(effects, duration, power, false, false, false));
    }
    private static void applyEffect(ServerPlayer player, Holder<@NotNull MobEffect> effects) {
        player.addEffect(new MobEffectInstance(effects, 100, 0, false, false, false));
    }
    private static void applyEffect(ServerPlayer player, Holder<@NotNull MobEffect> effects, int power) {
        player.addEffect(new MobEffectInstance(effects, 100, power, false, false, false));
    }
    private static <T extends Mob> List<T> getNearby(ServerPlayer player, Class<T> mob, double radius) {
        return player.level().getEntitiesOfClass(mob, player.getBoundingBox().inflate(radius));
    }
}
