package net.justmili.servertweaks.init;

import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.justmili.servertweaks.mechanics.events.*;

public class Events {
    public static void register() {
        EntityEvent.LIVING_HURT.register(Banishment::onEntityHurt);
        EntityEvent.LIVING_HURT.register(WhileAfk::onEntityHurt);
        EntityEvent.LIVING_HURT.register(WhileDuel::onEntityHurt);
        EntityEvent.LIVING_DEATH.register(WhileDuel::onPlayerDeath);
        TickEvent.SERVER_LEVEL_POST.register(Banishment::onWorldTick);
        TickEvent.PLAYER_POST.register(WhileAfk::onPlayerTick);
        EntityEvent.ADD.register(Banishment::onEntityLoad);
        PlayerEvent.PLAYER_JOIN.register(ScaleConvert::onServerJoined);
        PlayerEvent.PLAYER_QUIT.register(WhileDuel::onPlayerDisconnect);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(RightClickHarvest::onUseBlock);
    }
}