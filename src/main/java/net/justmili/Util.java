package net.justmili;

import net.minecraft.resources.Identifier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.Permissions;
import static net.justmili.servertweaks.ServerTweaks.MODID;

public class Util {
    public static Identifier asId(String path) {
        return Identifier.parse(MODID+":"+path);
    }
    //Fuck the new permission system, bring back integers
    public static boolean hasPerms(CommandSourceStack src, int level) {
        Permission permission = switch (level) {
            case 0 -> Permissions.COMMANDS_ENTITY_SELECTORS;
            case 1 -> Permissions.COMMANDS_MODERATOR;
            case 2 -> Permissions.COMMANDS_GAMEMASTER;
            case 3 -> Permissions.COMMANDS_ADMIN;
            case 4 -> Permissions.COMMANDS_OWNER;
            default -> Permissions.COMMANDS_OWNER;
        };

        return src.permissions().hasPermission(permission);
    }
}
