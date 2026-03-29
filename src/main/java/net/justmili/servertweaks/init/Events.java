package net.justmili.servertweaks.init;

import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.justmili.servertweaks.mechanics.abilities.AbilityEffects;
import net.justmili.servertweaks.mechanics.events.*;

public class Events {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(Banishment::onEntityHurt);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(WhileAfk::onEntityHurt);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(WhileDuel::onEntityHurt);
        EntityEvent.LIVING_DEATH.register(WhileDuel::onPlayerDeath);
        EntityEvent.ADD.register(Banishment::onEntityLoad);
        TickEvent.PLAYER_POST.register(Banishment::onPlayerTick);
        TickEvent.PLAYER_POST.register(WhileAfk::onPlayerTick);
        PlayerEvent.PLAYER_JOIN.register(ScaleConvert::onServerJoined);
        PlayerEvent.PLAYER_QUIT.register(WhileDuel::onPlayerDisconnect);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(RightClickHarvest::onUseBlock);
        AbilityEffects.registerAbilityEvents();
    }
}