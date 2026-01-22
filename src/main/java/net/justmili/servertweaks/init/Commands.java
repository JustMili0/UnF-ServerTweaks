package net.justmili.servertweaks.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.justmili.servertweaks.commands.*;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, environment) -> {
            GetDayCount.register(dispatcher, commandBuildContext, environment);
            DamageToggle.register(dispatcher, commandBuildContext, environment);
            ScaleMe.register(dispatcher, commandBuildContext, environment);
            ForceScale.register(dispatcher, commandBuildContext, environment);
            Banish.register(dispatcher, commandBuildContext, environment);
        });
    }
}