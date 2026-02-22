package net.justmili.servertweaks.mechanics.events;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.CommandUtil;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class WhileDuel {
    public static boolean onEntityHurt(LivingEntity entity, DamageSource source, float v) {
        //Only work if duel command is enabled
        if (!Config.enableDuelCommand.get()) return true;

        //Disable PVP in favor of Dueling
        if (!(source.getEntity() instanceof ServerPlayer duelingWith)) return true;
        if (!(entity instanceof ServerPlayer dueling)) return true;

        //Allow for people in duels to attack eachother
            //Save time of getting hit for "/duel end" timer
        if (FdaApiUtil.getBoolValue(dueling, PlayerAttachments.IN_DUEL)
            && duelingWith.getStringUUID().equals(FdaApiUtil.getStringValue(dueling, PlayerAttachments.DUELING_WITH))) {
            FdaApiUtil.setLongValue(dueling, PlayerAttachments.LAST_HIT_TIME, dueling.level().getGameTime());
            return true;
        }
        if (FdaApiUtil.getBoolValue(duelingWith, PlayerAttachments.IN_DUEL)
            && dueling.getStringUUID().equals(FdaApiUtil.getStringValue(duelingWith, PlayerAttachments.DUELING_WITH))) return true;

        //Return false otherwise (depends on config check)
        return false;
    }

    public static void onPlayerDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof ServerPlayer player)) return;
        if (!(source.getEntity() instanceof ServerPlayer opponent)) return;

        if (FdaApiUtil.getBoolValue(player, PlayerAttachments.IN_DUEL)) endDuel(player, opponent);
    }

    public static void onPlayerDisconnect(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        ServerPlayer player = handler.getPlayer();

        if (!FdaApiUtil.getBoolValue(player, PlayerAttachments.IN_DUEL)) return;

        String opponentUUID = FdaApiUtil.getStringValue(player, PlayerAttachments.DUELING_WITH);
        ServerPlayer opponent = server.getPlayerList()
            .getPlayer(java.util.UUID.fromString(opponentUUID));

        endDuel(player, opponent);

        if (opponent != null)
            CommandUtil.sendTo(opponent, "[ServerTweaks] Your opponent disconnected. The duel has ended.");
    }

    public static void endDuel(ServerPlayer player, ServerPlayer opponent) {
        FdaApiUtil.setBoolValue(player, PlayerAttachments.IN_DUEL, false);
        FdaApiUtil.setStringValue(player, PlayerAttachments.DUELING_WITH, "val_inactive");
        FdaApiUtil.setStringValue(player, PlayerAttachments.AWAITING_DUEL_SENDER, "val_inactive");
        FdaApiUtil.setStringValue(player, PlayerAttachments.AWAITING_DUEL_RECIPIENT, "val_inactive");
        FdaApiUtil.setLongValue(player, PlayerAttachments.LAST_HIT_TIME, 0L);

        if (opponent != null) {
            FdaApiUtil.setBoolValue(opponent, PlayerAttachments.IN_DUEL, false);
            FdaApiUtil.setStringValue(opponent, PlayerAttachments.DUELING_WITH, "val_inactive");
            FdaApiUtil.setStringValue(opponent, PlayerAttachments.AWAITING_DUEL_SENDER, "val_inactive");
            FdaApiUtil.setStringValue(opponent, PlayerAttachments.AWAITING_DUEL_RECIPIENT, "val_inactive");
            FdaApiUtil.setLongValue(opponent, PlayerAttachments.LAST_HIT_TIME, 0L);
        }
    }
}
