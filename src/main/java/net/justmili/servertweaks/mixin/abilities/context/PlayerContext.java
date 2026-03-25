package net.justmili.servertweaks.mixin.abilities.context;

import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

public final class PlayerContext {
    private static @Nullable Player interactingPlayer = null;

    public static void set(@Nullable Player player) { interactingPlayer = player; }
    public static @Nullable Player get() { return interactingPlayer; }
}