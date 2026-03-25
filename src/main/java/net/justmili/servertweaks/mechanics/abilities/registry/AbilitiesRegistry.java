package net.justmili.servertweaks.mechanics.abilities.registry;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.justmili.servertweaks.util.ScalerUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbilitiesRegistry {
    // Registry
    private static final Map<String, Ability> REGISTRY = new HashMap<>();

    public static final Ability BURNS_IN_DAYLIGHT = register(new BurnsInDaylight());          // FINISHED
    public static final Ability FIRE_IMMUNE = register(new Ability("FIRE_IMMUNE"));     // FINISHED
    public static final Ability FREEZE_IMMUNE = register(new Ability("FREEZE_IMMUNE")); // FINISHED
    public static final Ability CLIMBS_WALLS = register(new Ability("CLIMBS_WALLS"));   // FINISHED (UNTESTED)
    public static final Ability AQUA_GRACE = register(new AquaGrace());                       // FINISHED
    public static final Ability LIGHT = register(new Light());                                // FINISHED (UNTESTED)
    public static final Ability SWIFT = register(new Swift());                                // FINISHED
    public static final Ability HOPPY = register(new Hoppy());                                // FINISHED
    public static final Ability DWARF = register(new Dwarf());                                // FINISHED
    public static final Ability TOUGH = register(new Ability("TOUGH"));
    public static final Ability STRONG = register(new Ability("STRONG"));
    public static final Ability WEAK_TO_DAMAGE = register(new Ability("WEAK_TO_DAMAGE"));
    public static final Ability BREATHES_UNDERWATER = register(new Ability("BREATHES_UNDERWATER"));
    public static final Ability CANT_BREATHE_AIR = register(new Ability("CANT_BREATHE_AIR"));
    public static final Ability CANT_SWIM = register(new Ability("CANT_SWIM"));
    public static final Ability HYDROPHOBIC = register(new Ability("HYDROPHOBIC"));
    public static final Ability NIGHT_VISION = register(new Ability("NIGHT_VISION"));
    public static final Ability HUNTED_BY_FOX = register(new Ability("HUNTED_BY_FOX"));
    public static final Ability HUNTED_BY_WOLF = register(new Ability("HUNTED_BY_WOLF"));
    public static final Ability SCARES_CREEPERS = register(new Ability("SCARES_CREEPERS"));
    public static final Ability SCARES_PHANTOMS = register(new Ability("SCARES_PHANTOMS"));
    public static final Ability FRIENDS_WITH_NATURE = register(new Ability("FRIENDS_WITH_NATURE"));
    public static final Ability IS_MONSTER = register(new Ability("IS_MONSTER"));
    public static final Ability CARNIVORE = register(new Ability("CARNIVORE"));
    public static final Ability VEGETARIAN = register(new Ability("VEGETARIAN"));
    public static final Ability ONLY_EATS_SWEETS = register(new Ability("ONLY_EATS_SWEETS"));
    public static final Ability GRASS_EATER = register(new Ability("GRASS_EATER"));

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
            if (!(Config.playerAbilities.get())) return;
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
            if (ScalerUtil.wasScaled(player)) return;
            AttributeInstance scale = ScalerUtil.getScale(player);
            if (scale != null && scale.getBaseValue() > 0.75) ScalerUtil.setScale(player, 0.75);
            applyEffect(player, MobEffects.HASTE, 2);
        }
    }

    // TODO: Figure out what can be done via ticking, do the rest via mixins

    // Ticking abilities helper methods
    private static void applyEffect(ServerPlayer player, Holder<@NotNull MobEffect> effects, int duration, int power) {
        player.addEffect(new MobEffectInstance(effects, duration, power, false, false));
    }
    private static void applyEffect(ServerPlayer player, Holder<@NotNull MobEffect> effects) {
        player.addEffect(new MobEffectInstance(effects, 300, 0, false, false));
    }
    private static void applyEffect(ServerPlayer player, Holder<@NotNull MobEffect> effects, int power) {
        player.addEffect(new MobEffectInstance(effects, 300, power, false, false));
    }
}
