package net.justmili.servertweaks.mechanics.events;

import dev.architectury.event.EventResult;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.CommandUtil;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class WhileDuel {
    public static EventResult onEntityHurt(LivingEntity entity, DamageSource source, float v) {
        //Only work if duel command is enabled
        if (!Config.enableDuelCommand.get()) return EventResult.pass();

        //Disable PVP in favor of Dueling
        if (!(source.getEntity() instanceof ServerPlayer duelingWith)) return EventResult.pass();
        if (!(entity instanceof ServerPlayer dueling)) return EventResult.pass();

        //Allow for people in duels to attack eachother
            //Save time of getting hit for "/duel end" timer
        if (FdaApiUtil.getBoolValue(dueling, PlayerAttachments.IN_DUEL)
            && duelingWith.getStringUUID().equals(FdaApiUtil.getStringValue(dueling, PlayerAttachments.DUELING_WITH))) {
            FdaApiUtil.setLongValue(dueling, PlayerAttachments.LAST_HIT_TIME, dueling.level().getGameTime());
            return EventResult.pass();
        }
        if (FdaApiUtil.getBoolValue(duelingWith, PlayerAttachments.IN_DUEL)
            && dueling.getStringUUID().equals(FdaApiUtil.getStringValue(duelingWith, PlayerAttachments.DUELING_WITH))) return EventResult.pass();

        //Return false otherwise (depends on config check)
        return EventResult.interruptFalse();
    }

    public static EventResult onPlayerDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof ServerPlayer player)) return EventResult.pass();
        if (!(source.getEntity() instanceof ServerPlayer opponent)) return EventResult.pass();

        if (FdaApiUtil.getBoolValue(player, PlayerAttachments.IN_DUEL)) endDuel(player, opponent);

        return EventResult.pass();
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();

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
