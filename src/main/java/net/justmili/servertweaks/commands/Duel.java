package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.fdaapi.PlayerAttachments;
import net.justmili.servertweaks.mechanics.events.WhileDuel;
import net.justmili.servertweaks.util.CommandUtil;
import net.justmili.servertweaks.util.FdaApiUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class Duel {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("duel")
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        CommandUtil.checkIfPlayerExecuted(context);

                        ServerPlayer recipient = EntityArgument.getPlayer(context, "player");
                        ServerPlayer sender = context.getSource().getPlayer();

                        // Added return statements so the request doesn't go through when already dueling
                        if (FdaApiUtil.getBoolValue(sender, PlayerAttachments.IN_DUEL)) {
                            CommandUtil.sendTo(sender, "[ServerTweaks] You are already in a duel.");
                            return 0;
                        }
                        if (FdaApiUtil.getBoolValue(recipient, PlayerAttachments.IN_DUEL)) {
                            CommandUtil.sendTo(sender, "[ServerTweaks] " + recipient.getName().getString() + " is already in a duel.");
                            return 0;
                        }

                        CommandUtil.sendTo(sender, "[ServerTweaks] You've sent a duel request to " + recipient.getName().getString() + ".");
                        CommandUtil.sendTo(recipient, "[ServerTweaks] " + sender.getName().getString() + " has sent you a duel request.");

                        FdaApiUtil.setStringValue(sender, PlayerAttachments.AWAITING_DUEL_RECIPIENT, recipient.getStringUUID());
                        FdaApiUtil.setStringValue(recipient, PlayerAttachments.AWAITING_DUEL_SENDER, sender.getStringUUID());

                        return 1;
                    })
                )
                .then(Commands.literal("accept")
                    .executes(context -> {
                        CommandUtil.checkIfPlayerExecuted(context);

                        ServerPlayer recipient = context.getSource().getPlayer();
                        String senderUUID = FdaApiUtil.getStringValue(recipient, PlayerAttachments.AWAITING_DUEL_SENDER);

                        if (senderUUID != null && !senderUUID.equals("val_inactive")) {
                            ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(java.util.UUID.fromString(senderUUID));

                            if (sender == null) {
                                CommandUtil.sendTo(recipient, "[ServerTweaks] That player is no longer online.");
                                FdaApiUtil.setStringValue(recipient, PlayerAttachments.AWAITING_DUEL_SENDER, "val_inactive");
                                return 0;
                            }
                            FdaApiUtil.setStringValue(recipient, PlayerAttachments.DUELING_WITH, sender.getStringUUID());
                            FdaApiUtil.setStringValue(sender, PlayerAttachments.DUELING_WITH, recipient.getStringUUID());

                            FdaApiUtil.setBoolValue(recipient, PlayerAttachments.IN_DUEL, true);
                            FdaApiUtil.setBoolValue(sender, PlayerAttachments.IN_DUEL, true);

                            CommandUtil.sendTo(recipient, "[ServerTweaks] You are now in a duel with " + sender.getName().getString() + ".");
                            CommandUtil.sendTo(sender, "[ServerTweaks] You are now in a duel with " + recipient.getName().getString() + ".");

                            return 1;
                        }

                        return 0;
                    })
                )
                .then(Commands.literal("decline")
                    .executes(context -> {
                        CommandUtil.checkIfPlayerExecuted(context);

                        ServerPlayer recipient = context.getSource().getPlayer();
                        String senderUUID = FdaApiUtil.getStringValue(recipient, PlayerAttachments.AWAITING_DUEL_SENDER);

                        if (senderUUID != null && !senderUUID.equals("val_inactive")) {
                            ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(java.util.UUID.fromString(senderUUID));

                            FdaApiUtil.setStringValue(recipient, PlayerAttachments.AWAITING_DUEL_SENDER, "val_inactive");

                            if (sender == null) {
                                CommandUtil.sendTo(recipient, "[ServerTweaks] That player is no longer online.");
                                return 0;
                            }
                            FdaApiUtil.setStringValue(sender, PlayerAttachments.AWAITING_DUEL_RECIPIENT, "val_inactive");

                            CommandUtil.sendTo(recipient, "[ServerTweaks] You declined a duel with " + sender.getName().getString() + ".");
                            CommandUtil.sendTo(sender, "[ServerTweaks] " + recipient.getName().getString() + " declined your duel.");

                            return 1;
                        }

                        return 0;
                    })
                )
                .then(Commands.literal("end")
                    .executes(context -> {
                        CommandUtil.checkIfPlayerExecuted(context);

                        ServerPlayer player = context.getSource().getPlayer();

                        if (!FdaApiUtil.getBoolValue(player, PlayerAttachments.IN_DUEL)) {
                            CommandUtil.sendTo(player, "[ServerTweaks] You are not in a duel.");
                            return 0;
                        }

                        long ticksSinceHit = player.level().getGameTime() - FdaApiUtil.getLongValue(player, PlayerAttachments.LAST_HIT_TIME);
                        if (ticksSinceHit < 600) {
                            CommandUtil.sendTo(player, "[ServerTweaks] You cannot end a duel within 30 seconds of being hit.");
                            return 0;
                        }

                        String opponentUUID = FdaApiUtil.getStringValue(player, PlayerAttachments.DUELING_WITH);
                        ServerPlayer opponent = context.getSource().getServer().getPlayerList().getPlayer(java.util.UUID.fromString(opponentUUID));

                        WhileDuel.endDuel(player, opponent);

                        CommandUtil.sendTo(player, "[ServerTweaks] You have ended the duel.");
                        if (opponent != null) CommandUtil.sendTo(opponent, "[ServerTweaks] " + player.getName().getString() + " has ended the duel.");

                        return 1;
                    })
                )
        );
    }
}