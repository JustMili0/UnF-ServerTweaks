package net.justmili.servertweaks.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.scores.*;

public class ScalerUtil {
    //Converts old scoreboard scuff to fresh variables
    private static final String OBJECTIVE = "scaleLocked";

//    public static void convertScoreToVar(ServerPlayer player) {              //leftover method from trying to use CCAPI
//        Scoreboard sb = player.level().getServer().getScoreboard();
//        Objective obj = sb.getObjective(OBJECTIVE);
//        if (obj == null) return;
//
//        ScoreAccess score = sb.getOrCreatePlayerScore(
//            ScoreHolder.forNameOnly(player.getScoreboardName()),
//            obj
//        );
//
//        if (score.get() > 0) {
//            Variables vars = Components.VARIABLES.get(player);
//            vars.setScaleLocked(true);
//            Components.VARIABLES.sync(player);
//        }
//    }

    //Applies calculated scale
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

    //For checking and locking the scale via variables
    public static boolean isLocked(ServerPlayer player) {
        Scoreboard sb = player.level().getServer().getScoreboard();
        Objective obj = sb.getObjective(OBJECTIVE);
        if (obj == null) return false;

        ScoreAccess score = sb.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getScoreboardName()), obj);
        return score.get() > 0;
    }
    public static boolean setLocked(ServerPlayer player, boolean locked) throws CommandSyntaxException {
        MinecraftServer server = player.level().getServer();

        String playerName = player.getScoreboardName();
        boolean needsQuotes = playerName.contains(" ") || playerName.contains("\"");
        String playerToken = needsQuotes ? ("\"" + playerName.replace("\"", "\\\"") + "\"") : playerName;

        String cmd = String.format("scoreboard players set %s %s %d", playerToken, OBJECTIVE, locked ? 1 : 0);
        int result = server.getCommands().getDispatcher().execute(cmd, server.createCommandSourceStack());
        return result >= 0;
    }
}
