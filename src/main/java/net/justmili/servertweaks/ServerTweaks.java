package net.justmili.servertweaks;

import net.fabricmc.api.ModInitializer;
import net.justmili.servertweaks.init.Commands;
import net.justmili.servertweaks.init.Dimensions;
import net.justmili.servertweaks.mechanics.Events;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerTweaks implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(ServerTweaks.class);
    public static final String MODID = "servertweaks";

    @Override
    public void onInitialize() {
        Commands.register();
        Dimensions.register();
        Events.register();
    }
}
