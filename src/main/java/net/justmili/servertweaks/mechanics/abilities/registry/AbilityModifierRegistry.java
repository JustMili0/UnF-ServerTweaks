package net.justmili.servertweaks.mechanics.abilities.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.justmili.servertweaks.ServerTweaks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class AbilityModifierRegistry {
    public static final ResourceKey<Registry<Ability>> MODIFIERS = ResourceKey.createRegistryKey(ServerTweaks.asResource("ability_modifiers"));
    public static final Registry<Ability> REGISTRY = FabricRegistryBuilder.createSimple(MODIFIERS).buildAndRegister();
}
