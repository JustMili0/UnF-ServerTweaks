package net.justmili.servertweaks.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ScaleApplier {
    public static void applyScaleToPlayer(ServerPlayer player, double scale) {
        double min = 0.1;
        double max = 5.0;
        if (Double.isNaN(scale) || scale <= 0.0) scale = 1.0;
        scale = Math.max(min, Math.min(max, scale));

        AttributeInstance instance;
        try {
            instance = player.getAttribute(Attributes.SCALE);
        } catch (NoSuchFieldError | NoSuchMethodError e) {
            instance = null;
        }

        if (instance != null) {
            instance.setBaseValue(scale);
            player.refreshDimensions();
        }
    }
}
