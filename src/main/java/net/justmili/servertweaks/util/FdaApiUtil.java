package net.justmili.servertweaks.util;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerPlayer;

public class FdaApiUtil {
    public static boolean getBoolValue(ServerPlayer player, AttachmentType<Boolean> variable) {
        return Boolean.TRUE.equals(player.getAttached(variable));
    }
    public static void setBoolValue(ServerPlayer player, AttachmentType<Boolean> variable, boolean bool) {
        player.setAttached(variable, bool);
    }
}
