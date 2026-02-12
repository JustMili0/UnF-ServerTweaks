package net.justmili.servertweaks.mechanics.events;

import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

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
}
