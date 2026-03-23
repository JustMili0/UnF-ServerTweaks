package net.justmili.servertweaks.mechanics.abilities;

import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.mechanics.abilities.sets.AbilityModifiers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class AbilityManager {

    private static final String FILE_NAME = "servertweaks_abilities_config.dat";

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

        File file = getFile(server);
        if (!file.exists()) {
            playerAbilities.putAll(HARDCODED_ABILITIES);
            playerModifiers.putAll(HARDCODED_MODIFIERS);
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
                Set<Ability> abilities = new HashSet<>();
                for (String abilityName : abilitiesTag.keySet()) {
                    if (!abilitiesTag.getBooleanOr(abilityName, false)) continue;
                    Ability ability = AbilityRegistry.byName(abilityName);
                    if (ability != null) abilities.add(ability);
                }
                playerAbilities.put(uuid, abilities);

                CompoundTag modifiersTag = playerTag.getCompound("modifiers").orElseGet(CompoundTag::new);
                Set<AbilityModifiers> modifiers = EnumSet.noneOf(AbilityModifiers.class);
                for (AbilityModifiers modifier : AbilityModifiers.values()) {
                    if (modifiersTag.getBooleanOr(modifier.name(), false)) modifiers.add(modifier);
                }
                playerModifiers.put(uuid, modifiers);
            }

            playerAbilities.putAll(HARDCODED_ABILITIES);
            playerModifiers.putAll(HARDCODED_MODIFIERS);

            ServerTweaks.LOGGER.info("[AbilityManager] Loaded abilities for {} player(s).", playerAbilities.size());
        } catch (IOException e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to load .dat file: {}", e.getMessage());
        }
    }

    public static void save(MinecraftServer server) {
        CompoundTag root = new CompoundTag();

        Set<UUID> allUuids = new HashSet<>(playerAbilities.keySet());
        allUuids.addAll(playerModifiers.keySet());

        for (UUID uuid : allUuids) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putString("UUID", uuid.toString());

            CompoundTag abilitiesTag = new CompoundTag();
            Set<Ability> abilities = playerAbilities.getOrDefault(uuid, Collections.emptySet());
            for (Ability ability : abilities) {
                abilitiesTag.putBoolean(ability.getName(), true);
            }
            playerTag.put("abilities", abilitiesTag);

            CompoundTag modifiersTag = new CompoundTag();
            Set<AbilityModifiers> modifiers = playerModifiers.getOrDefault(uuid, Collections.emptySet());
            for (AbilityModifiers modifier : AbilityModifiers.values()) {
                modifiersTag.putBoolean(modifier.name(), modifiers.contains(modifier));
            }
            playerTag.put("modifiers", modifiersTag);

            root.put(uuid.toString(), playerTag);
        }

        try {
            File file = getFile(server);
            file.getParentFile().mkdirs();
            NbtIo.write(root, file.toPath());
            ServerTweaks.LOGGER.info("[AbilityManager] Saved abilities for {} player(s).", allUuids.size());
        } catch (IOException e) {
            ServerTweaks.LOGGER.error("[AbilityManager] Failed to save .dat file: {}", e.getMessage());
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

    private static File getFile(MinecraftServer server) {
        return new File(server.getServerDirectory().toFile(), "config/servertweaks/" + FILE_NAME);
    }
}