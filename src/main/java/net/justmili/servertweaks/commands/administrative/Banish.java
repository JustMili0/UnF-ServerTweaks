package net.justmili.servertweaks.commands.administrative;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.init.Dimensions;
import net.justmili.servertweaks.util.CommandUtil;
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
                .requires(src -> CommandUtil.hasPerms(src, 4))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer player = EntityArgument.getPlayer(context, "player");
                        CommandSourceStack source = context.getSource();
                        ServerLevel banishmentLevel = source.getServer().getLevel(Dimensions.BANISHMENT_WORLD);

                        if (banishmentLevel == null) {
                            CommandUtil.sendFail(source, "Banishment dimension is not loaded.");
                            return 0;
                        }

                        player.teleportTo(banishmentLevel, 0.5, 2.0, 0.5, Relative.DELTA, player.getYRot(), player.getXRot(), true);

                        CommandUtil.sendTo(player,
                            """
                                You have been banished.
                                There is no way out - no death or portal can ever save you.
                                This infinite world of darkness consumes everything that enters it, not even the void can escape itself.
                                """
                        );

                        return 1;
                    })
                )
        );
    }
}
