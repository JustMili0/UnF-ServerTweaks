package net.justmili.servertweaks.util;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;

import java.util.function.Supplier;

public class CommandUtil {
    //Fuck the new perms system, I want my numbers back
    public static boolean hasPerms(CommandSourceStack source, int level) {
        return source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(level)));
    }

    //Prevents commands from being ran from server console
    public static void checkIfPlayerExecuted(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (!(source.getEntity() instanceof ServerPlayer)) {
            sendFail(source, "Failed to execute \"" + context.getInput() + "\" - Command must be ran by a player.");
        }
    }

    //For if command is disabled on the server (USE AT COMMAND REGISTRATION)
    public static <T> boolean checkIfExpected(Supplier<T> configKey, boolean expected) {
        //I know it's a stupid one, and pretty unnecessary, but fuck you
        return configKey.get().equals(expected);
    }

    public static void sendSucc(CommandSourceStack source, String message) {
        source.sendSuccess(() -> Component.literal(message), false);
    }
    public static void sendFail(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal(message));
    }
    public static void sendTo(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
}
