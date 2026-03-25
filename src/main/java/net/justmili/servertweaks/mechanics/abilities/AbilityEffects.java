package net.justmili.servertweaks.mechanics.abilities;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilitiesRegistry;
import net.justmili.servertweaks.mechanics.abilities.registry.TickingAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Set;
import java.util.UUID;

public class AbilityEffects {
    public static void registerAbilityEvents() {
        if (!(Config.playerAbilities.get())) return;
        TickEvent.PLAYER_POST.register(AbilityEffects::onPlayerTick);
        EntityEvent.LIVING_HURT.register(AbilityEffects::onEntityHurt);
    }

    public static void onPlayerTick(Player ticking) {
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

    private static EventResult onEntityHurt(LivingEntity entity, DamageSource source, float v) {
        if (!(entity instanceof ServerPlayer player)) return EventResult.pass();
        Set<Ability> abilities = AbilityManager.getAbilities(player.getUUID());

        if (abilities.contains(AbilitiesRegistry.FIRE_IMMUNE) && source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)
            || source.is(DamageTypes.LAVA) || source.is(DamageTypes.HOT_FLOOR)) return EventResult.interruptFalse();
        if (abilities.contains(AbilitiesRegistry.BREATHES_UNDERWATER) && source.is(DamageTypes.DROWN)) return EventResult.interruptFalse();
        if (abilities.contains(AbilitiesRegistry.FREEZE_IMMUNE) && source.is(DamageTypes.FREEZE)) return EventResult.interruptFalse();

        return EventResult.pass();
    }

    public static boolean shouldClimb(ServerPlayer player) {
        if (!(Config.playerAbilities.get())) return false;
        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.CLIMBS_WALLS)) return false;
        if (player.onGround()) return false;
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).isSolid()) return true;
        }
        return false;
    }
}
