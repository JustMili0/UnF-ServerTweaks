package net.justmili.servertweaks.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.justmili.servertweaks.commands.*;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.util.CommandUtil;

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, environment) -> {
            if (CommandUtil.checkIfExpected(Config.enableScaleCommand, true))
                Scale.register(dispatcher, commandBuildContext, environment);
            if (CommandUtil.checkIfExpected(Config.enableAfkCommand, true))
                Afk.register(dispatcher, commandBuildContext, environment);
            if (CommandUtil.checkIfExpected(Config.enableDaycountCommand, true))
                DayCount.register(dispatcher, commandBuildContext, environment);
            if (CommandUtil.checkIfExpected(Config.enableDamageToggleCommand, true))
                DamageToggle.register(dispatcher, commandBuildContext, environment);
            if (CommandUtil.checkIfExpected(Config.enableBanishCommand, true))
                Banish.register(dispatcher, commandBuildContext, environment);
        });
    }
}