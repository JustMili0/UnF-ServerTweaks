package net.justmili.servertweaks.util;

import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;

public class ScalerUtil {

    //Converts old scoreboard scuff to fresh variables (purely for my own Minecraft server)
    public static void convertScoreToVar(ServerPlayer player) {
        if (player.getAttachedOrElse(PlayerAttachments.SCALE_LOCKED, false)) return;

        Scoreboard board = player.level().getServer().getScoreboard();
        Objective objective = board.getObjective("scaleLocked");
        if (objective == null) return;

        ScoreHolder holder = ScoreHolder.forNameOnly(player.getScoreboardName());
        ScoreAccess score = board.getOrCreatePlayerScore(holder, objective);

        // Migrate value
        if (score.get() > 0) {
            player.setAttached(PlayerAttachments.SCALE_LOCKED, true);
            board.resetSinglePlayerScore(holder, objective);
        }
    }

    //Applies calculated scale
    public static void applyScaleToPlayer(ServerPlayer player, double scale) {
        double min = 0.1;
        double max = 5.0;
        if (Double.isNaN(scale) || scale <= 0.0) scale = 1.0;
        scale = Math.max(min, Math.min(max, scale));

        AttributeInstance instance = player.getAttribute(Attributes.SCALE);
        if (instance != null) {
            instance.setBaseValue(scale);
            player.refreshDimensions();
        }
    }
}
