package net.justmili.servertweaks.mechanics.events;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WhileAfk {
    public static boolean onEntityHurt(LivingEntity entity, DamageSource source, float v) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return !FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK);
        }
        if (entity instanceof ServerPlayer player) {
            return !FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK);
        }
        return true;
    }

    public static void onPlayerTick(Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        //Teleport the player to saved position to prevent movement
        if (FdaApiUtil.getBoolValue(serverPlayer, PlayerAttachments.IS_AFK)) {
            double x = FdaApiUtil.getDoubleValue(serverPlayer, PlayerAttachments.AFK_X);
            double y = FdaApiUtil.getDoubleValue(serverPlayer, PlayerAttachments.AFK_Y);
            double z = FdaApiUtil.getDoubleValue(serverPlayer, PlayerAttachments.AFK_Z);

            player.setDeltaMovement(Vec3.ZERO);

            if (player.distanceToSqr(x, y, z) > 0.0001)
                serverPlayer.connection.teleport(x, y, z, serverPlayer.getYRot(), serverPlayer.getXRot());
        }

        //Set/reset command timer
        if (!FdaApiUtil.getBoolValue(serverPlayer, PlayerAttachments.IS_AFK) && Config.afkCommandCooldown.get() != 0) {
            int cooldown = FdaApiUtil.getIntValue(serverPlayer, PlayerAttachments.AFK_COOLDOWN);
            if (cooldown > 0) {
                FdaApiUtil.setIntValue(serverPlayer, PlayerAttachments.AFK_COOLDOWN, cooldown - 1);
            }
        }
    }
}
