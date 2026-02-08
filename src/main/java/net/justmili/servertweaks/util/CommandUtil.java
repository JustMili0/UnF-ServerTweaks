package net.justmili.servertweaks.util;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

public class CommandUtil {
    //Fuck the new perms system, I want my numbers back
    public static boolean hasPerms(CommandSourceStack src, int level) {
        return src.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }

    //Prevents commands from being ran from server console
    public static int failCheck(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer)) {
            /// Change source to get the command that was ran
            source.sendFailure(Component.literal("Failed to execute \"" + source + "\" - Command must be ran by a player."));
            return 0;
        }
        return 1;
    }

    public static void sendSucc(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), false);
    }
    public static void sendFail(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal(message));
    }
}
