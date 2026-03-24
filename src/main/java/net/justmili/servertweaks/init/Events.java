package net.justmili.servertweaks.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.justmili.servertweaks.mechanics.events.*;

public class Events {
    public static void register() {
        // TODO: convert to use Architectury API later on
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(Banishment::onEntityHurt);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(WhileAfk::onEntityHurt);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(WhileDuel::onEntityHurt);
        ServerLivingEntityEvents.AFTER_DEATH.register(WhileDuel::onPlayerDeath);
        ServerTickEvents.END_WORLD_TICK.register(Banishment::onWorldTick);
        ServerTickEvents.END_SERVER_TICK.register(WhileAfk::onServerTick);
        ServerEntityEvents.ENTITY_LOAD.register(Banishment::onEntityLoad);
        ServerPlayConnectionEvents.JOIN.register(ScaleConvert::onServerJoined);
        ServerPlayConnectionEvents.DISCONNECT.register(WhileDuel::onPlayerDisconnect);
        UseBlockCallback.EVENT.register(RightClickHarvest::onUseBlock);
        // For abilities use Architectury API events
    }
}