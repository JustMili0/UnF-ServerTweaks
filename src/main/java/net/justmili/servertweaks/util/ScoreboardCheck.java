package net.justmili.servertweaks.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.*;

public class ScoreboardCheck {
    private static final String OBJECTIVE = "scaleLocked";

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
