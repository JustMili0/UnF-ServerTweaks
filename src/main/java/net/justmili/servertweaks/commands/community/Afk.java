package net.justmili.servertweaks.commands.community;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.CommandUtil;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class Afk {
    private static final String AFK_PLAYERS = "afk_players";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("afk")
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                CommandUtil.failCheck(source);

                ServerLevel world = source.getLevel();
                ServerPlayer player = source.getPlayer();

                ServerScoreboard scoreboard = world.getScoreboard();
                PlayerTeam team = scoreboard.getPlayerTeam(AFK_PLAYERS);
                //Create team if doesn't exist
                if (team == null) {
                    team = scoreboard.addPlayerTeam(AFK_PLAYERS);
                    team.setNameTagVisibility(Team.Visibility.ALWAYS);
                    team.setPlayerPrefix(Component.literal("[AFK] "));
                    team.setColor(ChatFormatting.GRAY);
                }

                /*
                TODO:
                - Think of a way to stop the player from moving
                    (walk, sprint, jump, teleport. Without breaking any modifiers given from items)
                - Fix team add/remove
                */
                if (FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK)) {
                    scoreboard.removePlayerFromTeam(player.getScoreboardName(), team);
                    FdaApiUtil.setBoolValue(player, PlayerAttachments.IS_AFK, false);
                }
                if (!FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK)) {
                    scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
                    FdaApiUtil.setBoolValue(player, PlayerAttachments.IS_AFK, true);
                }
                return 1;
            })
        );
    }
}