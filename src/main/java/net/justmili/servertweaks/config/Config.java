package net.justmili.servertweaks.config;

import com.supermartijn642.configlib.api.ConfigBuilders;
import com.supermartijn642.configlib.api.IConfigBuilder;

import java.util.function.Supplier;

public class Config {
    //Individual configs for each command.
    public static final Supplier<Boolean> enableAfkCommand;
    public static final Supplier<Boolean> enableScaleCommand;
    ///public static final Supplier<Boolean> enableDuelCommand; //WIP Command
    public static final Supplier<Boolean> enableDaycountCommand;
    public static final Supplier<Boolean> enableDamageToggleCommand;
    public static final Supplier<Boolean> enableBanishCommand;

    //Config for "Afk" command.
    public static final Supplier<Boolean> despawnMonsters;
    public static final Supplier<Integer> afkCommandCooldown;

    // Config for "UncapSpeedLimits" mixin.
    public static final Supplier<Boolean> limitPlayerSpeed;
    public static final Supplier<Boolean> limitElytraSpeed;
    public static final Supplier<Boolean> limitVehicleSpeed;

    //Config for "RemoveAnvilLimit" mixin.
    public static final Supplier<Boolean> removeAnvilLimit;
    
    static {
        // construct a new config builder
        IConfigBuilder oldCfg = ConfigBuilders.newTomlConfig("servertweaks", "", false);
        oldCfg.comment("THIS CONFIG FILE IS NO LONGER SUPPORTED BY SERVERTWEAKS");
        IConfigBuilder builder = ConfigBuilders.newTomlConfig("servertweaks", "new", false);

        builder.push("Commands");
        builder.comment("Should these commands be enabled on the server?");
        enableAfkCommand = builder.define("enableAfkCommand", true);
        enableScaleCommand = builder.define("enableScaleCommand", true);
        ///enableDuelCommand = builder.define("enableDuelCommand", true);
        enableDaycountCommand = builder.define("enableDaycountCommand", true);
        enableDamageToggleCommand = builder.define("enableDamageToggleCommand", true);
        enableBanishCommand = builder.define("enableBanishCommand", true);
        builder.pop();

        builder.push("AFK-Command-Specific");
        despawnMonsters = builder.comment("Should \"wild\" monsters despawn around the player when coming out of AFK?")
            .define("despawnMonsters", true);
        afkCommandCooldown = builder.comment("Amount of time between the AFK command can be used again.")
            .define("afkCommandCooldown", 6000, 0, Integer.MAX_VALUE-255);
        builder.pop();

        builder.push("Mixins");
        limitPlayerSpeed = builder.comment("Should the server stop the player from moving too fast and print \"Player moved too fast!\" warn when on foot?")
            .define("limitPlayerSpeed", true);
        limitElytraSpeed = builder.comment("Should the server stop the player from flying too fast and print \"Player moved too fast!\" warn when on elytra?")
            .define("limitElytraSpeed", false);
        limitVehicleSpeed = builder.comment("Should the server stop the player from going too fast and print \"Player moved too fast!\" warn when in/on vehicle?")
            .define("limitVehicleSpeed", true);
        removeAnvilLimit = builder.comment("Should the server clamp the max anvil cost to 39 levels if at or over, to prevent \"Too Expensive\"?")
            .define("removeAnvilLimit", true);
        builder.pop();

        builder.build();
    }

}