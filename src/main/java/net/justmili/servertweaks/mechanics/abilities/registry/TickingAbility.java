package net.justmili.servertweaks.mechanics.abilities.registry;

import net.justmili.servertweaks.mechanics.abilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public abstract class TickingAbility extends Ability {

    public TickingAbility(String name) {
        super(name);
    }

    public abstract void tick(ServerPlayer player, ServerLevel level);
}