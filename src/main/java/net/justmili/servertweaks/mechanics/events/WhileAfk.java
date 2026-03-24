package net.justmili.servertweaks.mechanics.events;

import dev.architectury.event.EventResult;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WhileAfk {
    public static EventResult onEntityHurt(LivingEntity entity, DamageSource source, float v) {
        if (source.getEntity() instanceof ServerPlayer player) {
            return EventResult.interrupt(!FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK));
        }
        if (entity instanceof ServerPlayer player) {
            return EventResult.interrupt(!FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK));
        }
        return EventResult.pass();
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
