package net.justmili.servertweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.init.Commands;
import net.justmili.servertweaks.init.Dimensions;
import net.justmili.servertweaks.init.Events;
import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerTweaks implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(ServerTweaks.class);
    public static final String MODID = "servertweaks";

    @Override
    public void onInitialize() {
        LOGGER.info("Initilazing Server Tweaks...");
        PlayerAttachments.register();
        Commands.register();
        Dimensions.register();
        Events.register();

        ServerLifecycleEvents.SERVER_STARTED.register(AbilityManager::load);
        ServerLifecycleEvents.SERVER_STOPPING.register(AbilityManager::save);
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
    public static Identifier asPath(String path) {
        return Identifier.parse(path);
    }
}
