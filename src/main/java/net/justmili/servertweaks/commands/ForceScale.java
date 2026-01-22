package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import java.util.Collection;

import static net.justmili.servertweaks.util.ScaleApplier.applyScaleToPlayer;

public class ForceScale {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("forcescale")
                .requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                .then(Commands.argument("player", EntityArgument.players())
                    .then(Commands.argument("heightInCm", DoubleArgumentType.doubleArg(18.5, 2960.0))
                        .executes(ctx -> {
                            CommandSourceStack source = ctx.getSource();
                            double heightCm = DoubleArgumentType.getDouble(ctx, "heightInCm");
                            double scale = heightCm / 185.0;

                            Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "player");
                            for (ServerPlayer target : targets) { applyScaleToPlayer(target, scale); }

                            source.sendSuccess(() -> Component.literal(String.format("Applied scale %.3f (%.1f cm) to %d player(s).", scale, heightCm, targets.size())), false);
                            return targets.size();
                        })
                    )
                )
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.literal("Usage: '/scaleme <player> <height_cm>'."));
                    return 0;
                })
        );
    }
}