package net.justmili.servertweaks.mechanics.abilities.registry;

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
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.LightLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AbilityRegistry {
    private static final Map<String, Ability> BY_NAME = new HashMap<>();

    public static final Ability BURNS_IN_DAYLIGHT = register(new BurnsInDaylight());
    public static final Ability FIRE_IMMUNE = register(new Ability("FIRE_IMMUNE"));
    public static final Ability FREEZE_IMMUNE = register(new Ability("FREEZE_IMMUNE"));
    public static final Ability FALL_IMMUNE = register(new Ability("FALL_IMMUNE"));
    public static final Ability CLIMBS_WALLS = register(new Ability("CLIMBS_WALLS"));
    public static final Ability SPEED_IN_WATER = register(new SpeedInWater());
    public static final Ability BREATHES_UNDERWATER = register(new Ability("BREATHES_UNDERWATER"));
    public static final Ability NO_KNOCKBACK = register(new Ability("NO_KNOCKBACK"));
    public static final Ability STRONG = register(new Strong());
    public static final Ability HUNTED_BY_FOX = register(new HuntedByFox());
    public static final Ability HUNTED_BY_WOLF = register(new HuntedByWolf());
    public static final Ability SCARES_CREEPERS = register(new ScaresCreepers());
    public static final Ability SCARES_PHANTOMS = register(new ScaresPhantoms());
    public static final Ability FEARED_BY_PIGLINS = register(new FearedByPiglins());
    public static final Ability NIGHT_VISION = register(new NightVision());
    public static final Ability HATES_WATER = register(new HatesWater());
    public static final Ability ATTRACTS_BEES = register(new AttractsBees());
    public static final Ability FRIENDS_WITH_NATURE = register(new Ability("FRIENDS_WITH_NATURE"));
    public static final Ability IS_MONSTER = register(new IsMonster());
    public static final Ability JUMP_BOOST = register(new JumpBoost());
    public static final Ability CARNIVORE = register(new Ability("CARNIVORE"));
    public static final Ability VEGETARIAN = register(new Ability("VEGETARIAN"));
    public static final Ability ONLY_EATS_SWEETS = register(new Ability("ONLY_EATS_SWEETS"));
    public static final Ability CAN_FEED_FROM_GRASS = register(new Ability("CAN_FEED_FROM_GRASS"));

    private static <T extends Ability> T register(T ability) {
        BY_NAME.put(ability.getName(), ability);
        return ability;
    }
    public static Ability byName(String name) {
        return BY_NAME.get(name);
    }
    public static Collection<Ability> all() {
        return BY_NAME.values();
    }

    // Registered ticking abilities
    static class BurnsInDaylight extends TickingAbility {
        BurnsInDaylight() { super("BURNS_IN_DAYLIGHT"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (!level.isBrightOutside() || !level.canSeeSky(player.blockPosition())) return;
            int skyLight = level.getBrightness(LightLayer.SKY, player.blockPosition());
            if (skyLight <= 8) return;
            boolean hasHelmet = !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
            if (hasHelmet) return;
            if (player.isInWater()) {
                if (level.getGameTime() % 30 == 0) player.hurt(level.damageSources().inFire(), 0.5F);
            } else {
                player.setSharedFlagOnFire(true);
                player.setRemainingFireTicks(8);
            }
        }
    }

    static class NightVision extends TickingAbility {
        NightVision() { super("NIGHT_VISION"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (level.getGameTime() % 100 == 0)
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }

    static class JumpBoost extends TickingAbility {
        JumpBoost() { super("JUMP_BOOST"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (level.getGameTime() % 100 == 0)
                player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 300, 0, false, false));
        }
    }

    static class SpeedInWater extends TickingAbility {
        SpeedInWater() { super("SPEED_IN_WATER"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (!player.isInWater()) return;
            if (level.getGameTime() % 100 == 0)
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 300, 0, false, false));
        }
    }

    static class ScaresCreepers extends TickingAbility {
        ScaresCreepers() { super("SCARES_CREEPERS"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Creeper> nearby = level.getEntitiesOfClass(Creeper.class, player.getBoundingBox().inflate(6.0));
            for (Creeper creeper : nearby) {
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

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Phantom> nearby = level.getEntitiesOfClass(Phantom.class, player.getBoundingBox().inflate(16.0));
            for (Phantom phantom : nearby) {
                phantom.setTarget(null);
                phantom.getNavigation().moveTo(
                    phantom.getX() + (phantom.getX() - player.getX()),
                    phantom.getY() + 8,
                    phantom.getZ() + (phantom.getZ() - player.getZ()), 1.2);
            }
        }
    }

    static class FearedByPiglins extends TickingAbility {
        FearedByPiglins() { super("FEARED_BY_PIGLINS"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Piglin> nearby = level.getEntitiesOfClass(Piglin.class, player.getBoundingBox().inflate(8.0));
            for (Piglin piglin : nearby) {
                if (!piglin.isAdult()) continue;
                piglin.setTarget(null);
                piglin.getNavigation().moveTo(
                    piglin.getX() + (piglin.getX() - player.getX()), piglin.getY(),
                    piglin.getZ() + (piglin.getZ() - player.getZ()), 1.1);
            }
        }
    }

    static class HuntedByFox extends TickingAbility {
        HuntedByFox() { super("HUNTED_BY_FOX"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Fox> nearby = level.getEntitiesOfClass(Fox.class, player.getBoundingBox().inflate(16.0));
            for (Fox fox : nearby) {
                if (fox.getTarget() != null) continue;
                fox.setTarget(player);
            }
        }
    }

    static class HuntedByWolf extends TickingAbility {
        HuntedByWolf() { super("HUNTED_BY_WOLF"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Wolf> nearby = level.getEntitiesOfClass(Wolf.class, player.getBoundingBox().inflate(16.0));
            for (Wolf wolf : nearby) {
                if (wolf.isTame()) continue;
                if (wolf.getTarget() == null) wolf.setTarget(player);
            }
        }
    }

    static class AttractsBees extends TickingAbility {
        AttractsBees() { super("ATTRACTS_BEES"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Bee> nearby = level.getEntitiesOfClass(Bee.class, player.getBoundingBox().inflate(10.0));
            for (Bee bee : nearby) {
                if (bee.getTarget() != null) continue;
                if (bee.getNavigation().isDone()) bee.getNavigation().moveTo(player, 0.8);
            }
        }
    }

    static class HatesWater extends TickingAbility {
        HatesWater() { super("HATES_WATER"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            boolean inWaterOrRain = player.isInWater()
                || (level.isRaining() && level.canSeeSky(player.blockPosition()));
            if (inWaterOrRain && level.getGameTime() % 20 == 0)
                player.hurt(level.damageSources().drown(), 1.0F);
        }
    }

    static class Strong extends TickingAbility {
        Strong() { super("STRONG"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            if (level.getGameTime() % 10 != 0) return;
            int armor = player.getArmorValue();
            float maxHp = Math.max(40.0F, Math.min(100.0F, 100.0F - (armor * 3.0F)));
            player.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHp);
            if (player.getHealth() > maxHp) player.setHealth(maxHp);
        }
    }

    static class IsMonster extends TickingAbility {
        IsMonster() { super("IS_MONSTER"); }

        @Override
        public void tick(ServerPlayer player, ServerLevel level) {
            List<Villager> nearby = level.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(8.0));
            for (Villager villager : nearby) {
                villager.getNavigation().moveTo(
                    villager.getX() + (villager.getX() - player.getX()),
                    villager.getY(),
                    villager.getZ() + (villager.getZ() - player.getZ()), 1.2);
            }
        }
    }
}