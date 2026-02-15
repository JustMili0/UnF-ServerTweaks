package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.config.Config;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class Afk {
    private static final String AFK_PLAYERS = "afk_players";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("afk")
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                CommandUtil.checkIfPlayerExecuted(context);

                ServerLevel world = source.getLevel();
                ServerPlayer player = source.getPlayer();

                int cooldown = FdaApiUtil.getIntValue(player, PlayerAttachments.AFK_COOLDOWN);
                if (!FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK) && Config.afkCommandCooldown.get() != 0 && cooldown > 0) {
                    CommandUtil.sendFail(source, "[ServerTweaks] You must wait " + (cooldown / 20) + "s before using this command again.");
                    return 0;
                }

                ServerScoreboard scoreboard = world.getScoreboard();
                PlayerTeam team = scoreboard.getPlayerTeam(AFK_PLAYERS);

                //Create team if it doesn't exist
                if (team == null) {
                    team = scoreboard.addPlayerTeam(AFK_PLAYERS);
                    team.setNameTagVisibility(Team.Visibility.ALWAYS);
                    team.setPlayerPrefix(Component.literal("[AFK] "));
                    team.setColor(ChatFormatting.GRAY);
                }

                if (FdaApiUtil.getBoolValue(player, PlayerAttachments.IS_AFK)) {
                    //Remove from team and set IS_AFK to false
                    scoreboard.removePlayerFromTeam(player.getScoreboardName(), team);
                    FdaApiUtil.setBoolValue(player, PlayerAttachments.IS_AFK, false);

                    //Reset command cooldown
                    FdaApiUtil.setIntValue(player, PlayerAttachments.AFK_COOLDOWN, Config.afkCommandCooldown.get());

                    //If enabled, despawn
                    if (Config.despawnMonsters.get()) {
                        despawnNearbyMonsters(player);
                    }

                    CommandUtil.sendSucc(source, "[ServerTweaks] You are no longer AFK.");
                } else {
                    //Set position at which command was executed at
                    //Add to team and set IS_AFK to true
                    Vec3 pos = player.position();
                    FdaApiUtil.setDoubleValue(player, PlayerAttachments.AFK_X, pos.x);
                    FdaApiUtil.setDoubleValue(player, PlayerAttachments.AFK_Y, pos.y);
                    FdaApiUtil.setDoubleValue(player, PlayerAttachments.AFK_Z, pos.z);

                    scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
                    FdaApiUtil.setBoolValue(player, PlayerAttachments.IS_AFK, true);

                    CommandUtil.sendSucc(source, "[ServerTweaks] You are now AFK.");
                }

                return 1;
            })
        );
    }

    private static void despawnNearbyMonsters(ServerPlayer player) {
        ServerLevel level = player.level();
        if (level.isBrightOutside()) return;
        AABB box = new AABB(
            player.getX() - 8, player.getY() - 8, player.getZ() - 8,
            player.getX() + 8, player.getY() + 8, player.getZ() + 8
        );

        for (Monster monster : level.getEntitiesOfClass(Monster.class, box)) {
            if (monster.hasCustomName()) continue;
            if (monster.isPassenger()) continue;
            if (monster.isVehicle()) continue;

            monster.discard();
        }
    }
}