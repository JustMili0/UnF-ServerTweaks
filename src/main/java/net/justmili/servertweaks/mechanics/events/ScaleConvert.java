package net.justmili.servertweaks.mechanics.events;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.justmili.servertweaks.util.ScalerUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ScaleConvert {
    //Temp convert method
    public static void onServerJoined(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        ServerPlayer player = handler.getPlayer();
        ScalerUtil.convertScoreToVar(player);
    }
}
