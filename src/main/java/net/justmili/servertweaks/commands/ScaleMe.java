package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.justmili.servertweaks.util.ScoreboardCheck;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.justmili.servertweaks.util.ScaleApplier.applyScaleToPlayer;

public class ScaleMe {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("scaleme")
                .then(Commands.argument("heightInCm", DoubleArgumentType.doubleArg(80.0, 300.0))
                    .executes(ctx -> {
                        CommandSourceStack source = ctx.getSource();

                        if (!(source.getEntity() instanceof ServerPlayer player)) {
                            source.sendFailure(Component.literal("Command must be run by a player."));
                            return 0;
                        }

                        if (ScoreboardCheck.isLocked(player)) {
                            source.sendFailure(Component.literal("You can not change your height more than once."));
                            return 0;
                        }

                        double heightCm = DoubleArgumentType.getDouble(ctx, "heightInCm");
                        double scale = heightCm / 185.0;
                        applyScaleToPlayer(player, scale);
                        ScoreboardCheck.setLocked(player, true);

                        source.sendSuccess(() -> Component.literal(String.format("Your irl-to-game scale is %.3f (%.1f cm). It is now locked.", scale, heightCm)), false);
                        return 1;
                    })
                )
                .executes(ctx -> {
                    ctx.getSource().sendFailure(Component.literal("Usage: '/scaleme <height_cm>'."));
                    return 0;
                })
        );
    }
}
