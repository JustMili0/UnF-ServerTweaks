package net.justmili.servertweaks.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.justmili.servertweaks.commands.administrative.*;
import net.justmili.servertweaks.commands.community.*;
import net.justmili.servertweaks.commands.mixed.*;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, environment) -> {
            StScale.register(dispatcher, commandBuildContext, environment);
            Afk.register(dispatcher, commandBuildContext, environment);
            DayCount.register(dispatcher, commandBuildContext, environment);
            DamageToggle.register(dispatcher, commandBuildContext, environment);
            Banish.register(dispatcher, commandBuildContext, environment);
        });
    }
}