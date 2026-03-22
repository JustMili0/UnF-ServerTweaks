package net.justmili.servertweaks.mechanics.abilities;

import net.justmili.servertweaks.ServerTweaks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class AbilityManager {

    // config/servertweaks_abilities_config.dat
    private static final String FILE_NAME = "servertweaks_abilities_config.dat";

    // In-memory store: UUID -> set of active abilities
    private static final Map<UUID, Set<Ability>> playerAbilities = new HashMap<>();

    // Hardcoded defaults applied on load if the player has no entry yet
    private static final Map<UUID, Set<Ability>> HARDCODED = new HashMap<>();
    static {
        HARDCODED.put(
            UUID.fromString("19c3c783-9359-4311-98bf-79a6d361362d"), // SillyMili
            EnumSet.of(Ability.SCARES_CREEPERS, Ability.SCARES_PHANTOMS, Ability.CARNIVORE, Ability.MODIFIER_EDIBLE_GOLDEN_FOODS)
        );
        HARDCODED.put(
            UUID.fromString("3ca6c9e4-5727-46ea-bf8d-164d681ebe06"), // Zarsai
            EnumSet.of(Ability.HUNTED_BY_FOX, Ability.HUNTED_BY_WOLF, Ability.BURNS_IN_DAYLIGHT, Ability.JUMP_BOOST, Ability.VEGETARIAN, Ability.MODIFIER_EDIBLE_GOLDEN_FOODS)
        );
        HARDCODED.put(
            UUID.fromString("44c6e9dc-1ffe-4fb4-95e6-f9e95e013b94"), // Flufaye
            EnumSet.of(Ability.SCARES_CREEPERS, Ability.SCARES_PHANTOMS, Ability.ONLY_EATS_SWEETS, Ability.MODIFIER_EDIBLE_GOLDEN_FOODS)
        );
    }

    public static void load(MinecraftServer server) {
        playerAbilities.clear();

        File file = getFile(server);
        if (!file.exists()) {
            playerAbilities.putAll(HARDCODED);
            ServerTweaks.LOGGER.info("[AbilityManager] No .dat file found, using hardcoded defaults.");
            return;
        }

        try {
            CompoundTag root = NbtIo.read(file.toPath());
            if (root == null) return;

            for (String key : root.keySet()) {
                CompoundTag playerTag = root.getCompound(key).orElse(null);
                if (playerTag == null) continue;
                String uuidStr = playerTag.getString("UUID").orElse(null);
                if (uuidStr == null) continue;
                UUID uuid = UUID.fromString(uuidStr);
                CompoundTag abilitiesTag = playerTag.getCompound("abilities").orElseGet(CompoundTag::new);

                Set<Ability> abilities = EnumSet.noneOf(Ability.class);
                for (Ability ability : Ability.values()) {
                    if (abilitiesTag.getBooleanOr(ability.name(), false)) {
                        abilities.add(ability);
                    }
                }
                playerAbilities.put(uuid, abilities);
            }

            // Hardcoded entries override file entries for the specific UUIDs
            playerAbilities.putAll(HARDCODED);

            ServerTweaks.LOGGER.info("[AbilityManager] Loaded abilities for {} player(s).", playerAbilities.size());
        } catch (IOException e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to load .dat file: {}", e.getMessage());
        }
    }

    public static void save(MinecraftServer server) {
        CompoundTag root = new CompoundTag();

        for (Map.Entry<UUID, Set<Ability>> entry : playerAbilities.entrySet()) {
            UUID uuid = entry.getKey();
            Set<Ability> abilities = entry.getValue();

            // Use UUID as compound key since we don't track names here
            CompoundTag playerTag = new CompoundTag();
            playerTag.putString("UUID", uuid.toString());

            CompoundTag abilitiesTag = new CompoundTag();
            for (Ability ability : Ability.values()) {
                abilitiesTag.putBoolean(ability.name(), abilities.contains(ability));
            }
            playerTag.put("abilities", abilitiesTag);
            root.put(uuid.toString(), playerTag);
        }

        try {
            File file = getFile(server);
            file.getParentFile().mkdirs();
            NbtIo.write(root, file.toPath());
            ServerTweaks.LOGGER.info("[AbilityManager] Saved abilities for {} player(s).", playerAbilities.size());
        } catch (IOException e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to save .dat file: {}", e.getMessage());
        }
    }

    // Returns the ability set for a player, or empty set if they have no entry
    public static Set<Ability> getAbilities(UUID uuid) {
        return playerAbilities.getOrDefault(uuid, Collections.emptySet());
    }

    public static boolean has(UUID uuid, Ability ability) {
        return getAbilities(uuid).contains(ability);
    }

    private static File getFile(MinecraftServer server) {
        return new File(server.getServerDirectory().toFile(), "config/" + FILE_NAME);
    }
}