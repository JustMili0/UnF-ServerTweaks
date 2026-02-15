package net.justmili.servertweaks;

import net.fabricmc.api.ModInitializer;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.init.Commands;
import net.justmili.servertweaks.init.Dimensions;
import net.justmili.servertweaks.init.Events;
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
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
    public static Identifier asPath(String path) {
        return Identifier.parse(path);
    }
}
