package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.util.CommandUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

public class DayCount {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("daycount")
            .executes(context -> {
                CommandSourceStack source = context.getSource();
                ServerLevel world = source.getLevel();

                long dayTime = world.getDayTime() / 24000L;
                long gameTime = world.getGameTime() / 24000L;

                CommandUtil.sendSucc(source, "Day Time: " + dayTime + " day" + (dayTime == 1 ? "" : "s"));
                CommandUtil.sendSucc(source, "Game Time: " + gameTime + " day" + (gameTime == 1 ? "" : "s"));

                return 1;
            }));
    }
}
