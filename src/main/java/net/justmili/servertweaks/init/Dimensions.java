package net.justmili.servertweaks.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.justmili.servertweaks.ServerTweaks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

public final class Dimensions {
    public static final ResourceKey<Level> BANISHMENT_WORLD = ResourceKey.create(Registries.DIMENSION, ServerTweaks.asId("banishment"));

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
            server.getLevel(BANISHMENT_WORLD);
        });
    }
}
