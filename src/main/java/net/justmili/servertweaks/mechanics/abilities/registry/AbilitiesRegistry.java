package net.justmili.servertweaks.mechanics.abilities.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.justmili.servertweaks.ServerTweaks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class AbilitiesRegistry {
    public static final ResourceKey<Registry<Ability>> ABILITIES = ResourceKey.createRegistryKey(ServerTweaks.asResource("ability"));
    public static final Registry<Ability> REGISTRY = FabricRegistryBuilder.createSimple(ABILITIES).buildAndRegister();
}
