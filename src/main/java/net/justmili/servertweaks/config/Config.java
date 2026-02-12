package net.justmili.servertweaks.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Config {
    private static final String CFG_FILE = "servertweaks.toml";

    // Config for "UncapSpeedLimits" mixin.
    public static boolean limitPlayerSpeed = true;
    public static boolean limitElytraSpeed = false;
    public static boolean limitVehicleSpeed = true;

    //Config for "RemoveAnvilLimit" mixin.
    public static boolean removeAnvilLimit = true;

    //Config for "Afk" command.
    public static boolean despawnMonsters = true;
    public static int commandCooldown = 6000;

    //Load config
    public static void load(Path configDir) {
        Path configPath = configDir.resolve(CFG_FILE);
        if (Files.notExists(configPath)) {
            saveDefault(configPath);
        } else {
            loadConfig(configPath);
        }
    }

    //Load/save values
    private static void saveDefault(Path path) {
        StringBuilder content = new StringBuilder();
        content.append("# ServerTweaks Config\n\n");
        content.append("[UncapSpeedLimits.Mixin]\n");
        content.append("    limitPlayerSpeed = ").append(limitPlayerSpeed).append("\n");
        content.append("    limitElytraSpeed = ").append(limitElytraSpeed).append("\n");
        content.append("    limitVehicleSpeed = ").append(limitVehicleSpeed).append("\n\n");
        content.append("[RemoveAnvilLimit.Mixin]\n");
        content.append("    removeAnvilLimit = ").append(removeAnvilLimit).append("\n");
        content.append("[Afk.Command]");
        content.append("    despawnMonsters = ").append(despawnMonsters).append("\n");
        content.append("    commandCooldown = ").append(commandCooldown).append("\n");

        try {
            Files.writeString(path, content.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig(Path path) {
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                line = line.split("#")[0].trim();
                if (line.isEmpty() || !line.contains("=")) continue;

                String[] parts = line.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "limitPlayerSpeed" -> limitPlayerSpeed = Boolean.parseBoolean(value);
                    case "limitElytraSpeed" -> limitElytraSpeed = Boolean.parseBoolean(value);
                    case "limitVehicleSpeed" -> limitVehicleSpeed = Boolean.parseBoolean(value);
                    case "removeAnvilLimit" -> removeAnvilLimit = Boolean.parseBoolean(value);
                    case "despawnMonsters" -> despawnMonsters = Boolean.parseBoolean(value);
                    case "commandCooldown" -> commandCooldown = Integer.parseInt(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
