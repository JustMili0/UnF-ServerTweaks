package net.justmili.servertweaks.mechanics.abilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.justmili.servertweaks.mechanics.abilities.ability.AbilityModifier;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilitiesRegistry;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilityModifierRegistry;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.justmili.servertweaks.util.JsonUtil.GSON;

public class AbilityManager {
    private static final String FILE_NAME = "player_abilities.json";
    private static final Map<UUID, Set<Ability>> playerAbilities = new HashMap<>();
    private static final Map<UUID, Set<AbilityModifier>> playerModifiers = new HashMap<>();
    private static final Map<UUID, Set<Ability>> HARDCODED_ABILITIES = new HashMap<>(); /// To hardcode behavior for some players
    private static final Map<UUID, Set<AbilityModifier>> HARDCODED_MODIFIERS = new HashMap<>();

    static {
        // Flufaye
        HARDCODED_ABILITIES.put(UUID.fromString("44c6e9dc-1ffe-4fb4-95e6-f9e95e013b94"), new HashSet<>(List.of(
            AbilitiesRegistry.SCARES_CREEPERS, AbilitiesRegistry.SCARES_PHANTOMS, AbilitiesRegistry.ONLY_EATS_SWEETS,
            AbilitiesRegistry.FRIENDS_WITH_NATURE, AbilitiesRegistry.WEAK_TO_DAMAGE)));
        HARDCODED_MODIFIERS.put(UUID.fromString("44c6e9dc-1ffe-4fb4-95e6-f9e95e013b94"),
            Set.of(AbilityModifierRegistry.ADD_GOLD_FOODS_TO_DIET));

        // Zarsai
        HARDCODED_ABILITIES.put(UUID.fromString("3ca6c9e4-5727-46ea-bf8d-164d681ebe06"), new HashSet<>(List.of(
            AbilitiesRegistry.HUNTED_BY_FOX, AbilitiesRegistry.HUNTED_BY_WOLF, AbilitiesRegistry.BURNS_IN_DAYLIGHT,
            AbilitiesRegistry.HOPPY, AbilitiesRegistry.VEGETARIAN, AbilitiesRegistry.IS_MONSTER)));
        HARDCODED_MODIFIERS.put(UUID.fromString("3ca6c9e4-5727-46ea-bf8d-164d681ebe06"),
            Set.of(AbilityModifierRegistry.ADD_GOLD_FOODS_TO_DIET));

        // SillyMili
        HARDCODED_ABILITIES.put(UUID.fromString("19c3c783-9359-4311-98bf-79a6d361362d"), new HashSet<>(List.of(
            AbilitiesRegistry.SCARES_CREEPERS, AbilitiesRegistry.SCARES_PHANTOMS, AbilitiesRegistry.CARNIVORE,
            AbilitiesRegistry.CANT_SWIM)));
        HARDCODED_MODIFIERS.put(UUID.fromString("19c3c783-9359-4311-98bf-79a6d361362d"),
            Set.of(AbilityModifierRegistry.ADD_GOLD_FOODS_TO_DIET));
    }

    public static void loadFile(MinecraftServer server) {
        playerAbilities.clear();
        playerModifiers.clear();
        if (!(Config.playerAbilities.get())) {
            HARDCODED_ABILITIES.clear();
            HARDCODED_MODIFIERS.clear();
            return;
        }

        // Create file with default hardcoded
        File file = getFile();
        if (!file.exists()) {
            playerAbilities.putAll(HARDCODED_ABILITIES);
            playerModifiers.putAll(HARDCODED_MODIFIERS);

            saveFile(server);
            return;
        }

        // Fallback if reader fails
        playerAbilities.putAll(HARDCODED_ABILITIES);
        playerModifiers.putAll(HARDCODED_MODIFIERS);

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            for (var entry : root.entrySet()) {
                UUID uuid;
                try { uuid = UUID.fromString(entry.getKey()); }
                catch (IllegalArgumentException e) {
                    ServerTweaks.LOGGER.warn("[AbilityManager] Invalid UUID '{}', skipping.", entry.getKey());
                    continue;
                }

                JsonObject object = entry.getValue().getAsJsonObject();
                Set<Ability> abilities = new LinkedHashSet<>();
                if (object.has("abilities")) {
                    for (var element : object.getAsJsonArray("abilities")) {
                        Ability ability = AbilitiesRegistry.byName(element.getAsString());
                        if (ability == null) { 
                            ServerTweaks.LOGGER.warn("[AbilityManager] Unknown ability '{}', skipping.", element.getAsString()); 
                            continue; 
                        }
                        abilities.add(ability);
                    }
                }
                Set<AbilityModifier> modifiers = new LinkedHashSet<>();
                if (object.has("ability_modifiers")) {
                    for (var element : object.getAsJsonArray("ability_modifiers")) {
                        AbilityModifier modifier = AbilityModifierRegistry.byName(element.getAsString());
                        if (modifier == null) { 
                            ServerTweaks.LOGGER.warn("[AbilityManager] Unknown modifier '{}', skipping.", element.getAsString()); continue; 
                        }
                        modifiers.add(modifier);
                    }
                }
                playerAbilities.put(uuid, abilities);
                playerModifiers.put(uuid, modifiers);
            }
        } catch (Exception e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to read config: {}", e.getMessage());
        }
    }
    public static void saveFile(MinecraftServer server) {
        JsonObject root = new JsonObject();

        Set<UUID> allUuids = new HashSet<>(playerAbilities.keySet());
        allUuids.addAll(playerModifiers.keySet());

        Map<UUID, String> names = new HashMap<>();
        names.put(UUID.fromString("19c3c783-9359-4311-98bf-79a6d361362d"), "SillyMili");
        names.put(UUID.fromString("3ca6c9e4-5727-46ea-bf8d-164d681ebe06"), "Zarsai");
        names.put(UUID.fromString("44c6e9dc-1ffe-4fb4-95e6-f9e95e013b94"), "Flufaye");

        for (UUID uuid : allUuids) {
            JsonObject playerObj = new JsonObject();

            if (names.containsKey(uuid)) playerObj.addProperty("name", names.get(uuid));

            JsonArray abilitiesArr = new JsonArray();
            Set<Ability> abilities = playerAbilities.getOrDefault(uuid, Collections.emptySet());
            abilities.stream().map(Ability::getName).sorted().forEach(abilitiesArr::add);
            playerObj.add("abilities", abilitiesArr);

            JsonArray modifiersArr = new JsonArray();
            Set<AbilityModifier> modifiers = playerModifiers.getOrDefault(uuid, Collections.emptySet());
            modifiers.stream().map(AbilityModifier::getName).sorted().forEach(modifiersArr::add);
            playerObj.add("ability_modifiers", modifiersArr);

            root.add(uuid.toString(), playerObj);
        }

        try {
            File file = getFile();
            file.getParentFile().mkdirs();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
            ServerTweaks.LOGGER.info("[AbilityManager] Saved abilities for {} player(s).", allUuids.size());
        } catch (Exception e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to save config: {}", e.getMessage());
        }
    }

    public static Set<Ability> getAbilities(UUID uuid) {
        return playerAbilities.getOrDefault(uuid, Collections.emptySet());
    }
    public static Set<AbilityModifier> getModifiers(UUID uuid) {
        return playerModifiers.getOrDefault(uuid, Collections.emptySet());
    }
    public static boolean has(UUID uuid, Ability ability) {
        return getAbilities(uuid).contains(ability);
    }
    public static boolean has(UUID uuid, AbilityModifier modifier) {
        return getModifiers(uuid).contains(modifier);
    }

    private static File getFile() {
        return new File("config/servertweaks/" + FILE_NAME);
    }
}
