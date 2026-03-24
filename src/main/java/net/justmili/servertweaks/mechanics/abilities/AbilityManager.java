package net.justmili.servertweaks.mechanics.abilities;

import com.google.gson.*;
import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.mechanics.abilities.registry.Ability;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilityRegistry;
import net.minecraft.server.MinecraftServer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class AbilityManager {

    private static final String FILE_NAME = "player_abilities.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<UUID, Set<Ability>> playerAbilities = new HashMap<>();
    private static final Map<UUID, Set<AbilityModifiers>> playerModifiers = new HashMap<>();

    private static final Map<UUID, Set<Ability>> HARDCODED_ABILITIES = new HashMap<>();
    private static final Map<UUID, Set<AbilityModifiers>> HARDCODED_MODIFIERS = new HashMap<>();
    static {
        // SillyMili
        HARDCODED_ABILITIES.put(UUID.fromString("19c3c783-9359-4311-98bf-79a6d361362d"), new HashSet<>(List.of(
            AbilityRegistry.SCARES_CREEPERS, AbilityRegistry.SCARES_PHANTOMS, AbilityRegistry.CARNIVORE)));
        HARDCODED_MODIFIERS.put(UUID.fromString("19c3c783-9359-4311-98bf-79a6d361362d"),
            EnumSet.of(AbilityModifiers.ADD_GOLD_FOODS_TO_DIET));

        // Zarsai
        HARDCODED_ABILITIES.put(UUID.fromString("3ca6c9e4-5727-46ea-bf8d-164d681ebe06"), new HashSet<>(List.of(
            AbilityRegistry.HUNTED_BY_FOX, AbilityRegistry.HUNTED_BY_WOLF, AbilityRegistry.BURNS_IN_DAYLIGHT,
            AbilityRegistry.JUMP_BOOST, AbilityRegistry.VEGETARIAN, AbilityRegistry.IS_MONSTER)));
        HARDCODED_MODIFIERS.put(UUID.fromString("3ca6c9e4-5727-46ea-bf8d-164d681ebe06"),
            EnumSet.of(AbilityModifiers.ADD_GOLD_FOODS_TO_DIET));

        // Flufaye
        HARDCODED_ABILITIES.put(UUID.fromString("44c6e9dc-1ffe-4fb4-95e6-f9e95e013b94"), new HashSet<>(List.of(
            AbilityRegistry.SCARES_CREEPERS, AbilityRegistry.SCARES_PHANTOMS,
            AbilityRegistry.ONLY_EATS_SWEETS, AbilityRegistry.FRIENDS_WITH_NATURE)));
        HARDCODED_MODIFIERS.put(UUID.fromString("44c6e9dc-1ffe-4fb4-95e6-f9e95e013b94"),
            EnumSet.of(AbilityModifiers.ADD_GOLD_FOODS_TO_DIET));
    }

    public static void load(MinecraftServer server) {
        playerAbilities.clear();
        playerModifiers.clear();

        File file = getFile();
        if (!file.exists()) {
            playerAbilities.putAll(HARDCODED_ABILITIES);
            playerModifiers.putAll(HARDCODED_MODIFIERS);
            ServerTweaks.LOGGER.info("[AbilityManager] No config found, using hardcoded defaults.");
            save(server); // Write the hardcoded entries so the file gets created
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null) return;

            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                UUID uuid = UUID.fromString(entry.getKey());
                JsonObject playerObj = entry.getValue().getAsJsonObject();

                Set<Ability> abilities = new HashSet<>();
                if (playerObj.has("abilities")) {
                    for (JsonElement el : playerObj.getAsJsonArray("abilities")) {
                        Ability ability = AbilityRegistry.byName(el.getAsString());
                        if (ability != null) abilities.add(ability);
                        else ServerTweaks.LOGGER.warn("[AbilityManager] Unknown ability '{}' for {}", el.getAsString(), uuid);
                    }
                }
                playerAbilities.put(uuid, abilities);

                Set<AbilityModifiers> modifiers = EnumSet.noneOf(AbilityModifiers.class);
                if (playerObj.has("ability_modifiers")) {
                    for (JsonElement el : playerObj.getAsJsonArray("ability_modifiers")) {
                        try {
                            modifiers.add(AbilityModifiers.valueOf(el.getAsString()));
                        } catch (IllegalArgumentException e) {
                            ServerTweaks.LOGGER.warn("[AbilityManager] Unknown modifier '{}' for {}", el.getAsString(), uuid);
                        }
                    }
                }
                playerModifiers.put(uuid, modifiers);
            }

            // Hardcoded entries always override file entries for those UUIDs
            playerAbilities.putAll(HARDCODED_ABILITIES);
            playerModifiers.putAll(HARDCODED_MODIFIERS);

            ServerTweaks.LOGGER.info("[AbilityManager] Loaded abilities for {} player(s).", playerAbilities.size());
        } catch (Exception e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to load config: {}", e.getMessage());
        }
    }

    public static void save(MinecraftServer server) {
        JsonObject root = new JsonObject();

        // Collect all UUIDs, merge abilities and modifiers maps
        Set<UUID> allUuids = new HashSet<>(playerAbilities.keySet());
        allUuids.addAll(playerModifiers.keySet());

        // Build a name lookup for UUIDs from hardcoded so the "name" field is accurate
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
            Set<AbilityModifiers> modifiers = playerModifiers.getOrDefault(uuid, Collections.emptySet());
            modifiers.stream().map(Enum::name).sorted().forEach(modifiersArr::add);
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

    public static Set<AbilityModifiers> getModifiers(UUID uuid) {
        return playerModifiers.getOrDefault(uuid, Collections.emptySet());
    }

    public static boolean has(UUID uuid, Ability ability) {
        return getAbilities(uuid).contains(ability);
    }

    public static boolean has(UUID uuid, AbilityModifiers modifier) {
        return getModifiers(uuid).contains(modifier);
    }

    private static File getFile() {
        return new File("config/servertweaks/" + FILE_NAME);
    }
}