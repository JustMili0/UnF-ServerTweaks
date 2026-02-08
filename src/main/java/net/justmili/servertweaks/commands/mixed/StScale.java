package net.justmili.servertweaks.commands.mixed;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.util.CommandUtil;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.justmili.servertweaks.util.ScalerUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

import static net.justmili.servertweaks.util.ScalerUtil.applyScaleToPlayer;

public class StScale {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("scale")
                .then(Commands.argument("height_cm", DoubleArgumentType.doubleArg(80.0, 300.0))
                    .executes(context -> {
                        CommandSourceStack source = context.getSource();
                        ServerPlayer player = source.getPlayer();

                        CommandUtil.failCheck(source);

                        if (FdaApiUtil.getBoolValue(player, PlayerAttachments.SCALE_LOCKED)) {
                            CommandUtil.sendFail(source, "You can not change your height more than once.");
                            return 0;
                        }

                        double heightCm = DoubleArgumentType.getDouble(context, "height_cm");
                        double scale = heightCm / 185.0;
                        ScalerUtil.applyScaleToPlayer(player, scale);
                        FdaApiUtil.setBoolValue(player, PlayerAttachments.SCALE_LOCKED, true);

                        CommandUtil.sendSucc(source, String.format("Your irl-to-game scale is %.3f (%.1f cm). It is now locked.", scale, heightCm));
                        return 1;
                    })
                )
                .then(Commands.literal("force")
                    .requires(src -> CommandUtil.hasPerms(src, 4))
                    .then(Commands.argument("player", EntityArgument.players())
                        .then(Commands.argument("height_cm", DoubleArgumentType.doubleArg(18.5, 2960.0))
                            .executes(context -> {
                                CommandSourceStack source = context.getSource();
                                double heightCm = DoubleArgumentType.getDouble(context, "height_cm");
                                double scale = heightCm / 185.0;

                                Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
                                for (ServerPlayer player : players) {
                                    applyScaleToPlayer(player, scale);
                                }

                                CommandUtil.sendSucc(source, String.format("Applied scale %.3f (%.1f cm) to %d player(s).", scale, heightCm, players.size()));
                                return players.size();
                            })
                        )
                    )
                )
                .then(Commands.literal("unlock")
                    .requires(src -> CommandUtil.hasPerms(src, 4))
                    .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
                            for (ServerPlayer player : players) {
                                FdaApiUtil.setBoolValue(player, PlayerAttachments.SCALE_LOCKED, false);
                            }

                            CommandUtil.sendSucc(source, String.format("Unlocked scale modification for %d player(s).", players.size()));
                            return players.size();
                        })
                    )
                )
                .then(Commands.literal("reset")
                    .requires(src -> CommandUtil.hasPerms(src, 4))
                    .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
                            for (ServerPlayer player : players) {
                                applyScaleToPlayer(player, 1.0);
                                FdaApiUtil.setBoolValue(player, PlayerAttachments.SCALE_LOCKED, false);
                            }

                            CommandUtil.sendSucc(source, String.format("Reset scale and unlocked scale modifications for %d player(s).", players.size()));
                            return players.size();
                        })
                    )
                )
                .then(Commands.literal("reset-nounlock")
                    .requires(src -> CommandUtil.hasPerms(src, 4))
                    .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            Collection<ServerPlayer> players = EntityArgument.getPlayers(context, "player");
                            for (ServerPlayer player : players) {
                                applyScaleToPlayer(player, 1.0);
                            }

                            CommandUtil.sendSucc(source, String.format("Reset scale for %d player(s).", players.size()));
                            return players.size();
                        })
                    )
                )
        );
    }
}
