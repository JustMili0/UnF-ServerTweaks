package net.justmili.servertweaks.mechanics.abilities.registry;

import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.LightLayer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbilitiesRegistry {
    // Registry
    private static final Map<String, Ability> REGISTRY = new HashMap<>();

    public static final Ability BURNS_IN_DAYLIGHT = register(new BurnsInDaylight());
    public static final Ability FIRE_IMMUNE = register(new Ability("FIRE_IMMUNE"));
    public static final Ability FREEZE_IMMUNE = register(new Ability("FREEZE_IMMUNE"));
    public static final Ability CLIMBS_WALLS = register(new Ability("CLIMBS_WALLS"));
    public static final Ability AQUA_GRACE = register(new Ability("AQUA_GRACE"));
    public static final Ability LIGHT = register(new Ability("LIGHT"));
    public static final Ability SWIFT = register(new Ability("SWIFT"));
    public static final Ability HOPPY = register(new Ability("HOPPY"));
    public static final Ability DWARF = register(new Ability("DWARF"));
    public static final Ability TOUGH = register(new Ability("TOUGH"));
    public static final Ability STRONG = register(new Ability("STRONG"));
    public static final Ability WEAK_TO_DAMAGE = register(new Ability("WEAK_TO_DAMAGE")); // phone edit note: 1.15x damage multiplier
    public static final Ability BREATHS_UNDERWATER = register(new Ability("BREATHS_UNDERWATER"));
    public static final Ability CANT_BREATHE_AIR = register(new Ability("CANT_BREATHE_AIR"));
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

    // TODO: Figure out what can be done via ticking, do the rest via mixins
}
