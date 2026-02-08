package net.justmili.servertweaks.init;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.justmili.servertweaks.mechanics.events.Banishment;
import net.justmili.servertweaks.mechanics.events.ScaleConvert;
import net.justmili.servertweaks.mechanics.events.WhileAfk;

public class Events {
    public static void register() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(Banishment::onEntityHurt);
        ServerLivingEntityEvents.ALLOW_DAMAGE.register(WhileAfk::onEntityHurt);
        ServerTickEvents.END_WORLD_TICK.register(Banishment::onWorldTick);
        ServerEntityEvents.ENTITY_LOAD.register(Banishment::onEntityLoad);
        ServerPlayConnectionEvents.JOIN.register(ScaleConvert::onServerJoined);
    }
}
