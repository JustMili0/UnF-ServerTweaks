package net.justmili.servertweaks;

import net.fabricmc.api.ModInitializer;
import net.justmili.servertweaks.init.Commands;
import net.justmili.servertweaks.init.Dimensions;
import net.justmili.servertweaks.mechanics.Events;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerTweaks implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(ServerTweaks.class);
    public static final String MODID = "servertweaks";

    @Override
    public void onInitialize() {
        LOGGER.info("Initilazing Server Tweaks...");
        Commands.register();
        Dimensions.register();
        Events.register();
    }

    public static Identifier asId(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
    public static Identifier asPath(String path) {
        return Identifier.parse(path);
    }
    //Fuck the new permission system, bring back integers
    public static boolean hasPerms(CommandSourceStack src, int level) {
        return src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }
}
