package net.justmili.servertweaks.mechanics.abilities.registry;

import net.justmili.servertweaks.mechanics.abilities.ability.AbilityModifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AbilityModifierRegistry {
    // Registry
    private static final Map<String, AbilityModifier> REGISTRY = new HashMap<>();

    public static final AbilityModifier ADD_GOLD_FOODS_TO_DIET = register(new AbilityModifier("ADD_GOLD_FOODS_TO_DIET"));

    private static AbilityModifier register(AbilityModifier modifier) {
        REGISTRY.put(modifier.getName(), modifier);
        return modifier;
    }
    public static @Nullable AbilityModifier byName(String name) {
        return REGISTRY.get(name);
    }
}
