package net.justmili.servertweaks.network;

import net.justmili.Util;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

public final class Components {
    public static final ComponentKey<Variables> VARIABLES = ComponentRegistry.getOrCreate(Util.asId("variables"), Variables.class);

    private Components() {}
}
