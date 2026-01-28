package net.justmili.servertweaks.util;

import net.justmili.servertweaks.network.Components;
import net.justmili.servertweaks.network.Variables;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.*;

public class ScalerUtil {
    //Converts old scoreboard scuff to fresh variables
    private static final String OBJECTIVE = "scaleLocked";

    public static void convertScoreToVar(ServerPlayer player) {
        Scoreboard sb = player.level().getServer().getScoreboard();
        Objective obj = sb.getObjective(OBJECTIVE);
        if (obj == null) return;

        ScoreAccess score = sb.getOrCreatePlayerScore(
            ScoreHolder.forNameOnly(player.getScoreboardName()),
            obj
        );

        if (score.get() > 0) {
            Variables vars = Components.VARIABLES.get(player);
            vars.setScaleLocked(true);
            Components.VARIABLES.sync(player);
        }
    }

    //Applies calculated scale
    public static void applyScaleToPlayer(ServerPlayer player, double scale) {
        double min = 0.1;
        double max = 5.0;
        if (Double.isNaN(scale) || scale <= 0.0) scale = 1.0;
        scale = Math.max(min, Math.min(max, scale));

        var instance = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.SCALE);
        if (instance != null) {
            instance.setBaseValue(scale);
            player.refreshDimensions();
        }
    }

    //For checking and locking the scale via variables
    public static boolean isLocked(ServerPlayer player) {
        return Components.VARIABLES.get(player).isScaleLocked();
    }
    public static void setLocked(ServerPlayer player, boolean locked) {
        Variables vars = Components.VARIABLES.get(player);
        vars.setScaleLocked(locked);
        Components.VARIABLES.sync(player);
    }
}
