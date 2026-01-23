package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.init.Dimensions;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Relative;

public class Banish {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("banish")
                .requires(src -> ServerTweaks.hasPerms(src, 4))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                        CommandSourceStack source = ctx.getSource();
                        ServerLevel banishmentLevel = source.getServer().getLevel(Dimensions.BANISHMENT_WORLD);

                        if (banishmentLevel == null) {
                            source.sendFailure(Component.literal("Banishment dimension is not loaded."));
                            return 0;
                        }

                        target.teleportTo(banishmentLevel, 0.5, 2.0, 0.5, Relative.DELTA, target.getYRot(), target.getXRot(), true);

                        target.sendSystemMessage(
                            Component.literal("You have been banished. " +
                                    "There is no way out - no death or portal can ever save you. " +
                                    "This infinite world of darkness consumes everything that enters it, " +
                                    "not even the void can escape itself.")
                        );

                        return 1;
                    })
                )
        );
    }
}
