package net.justmili.servertweaks.mechanics.abilities.ability;

import java.util.Objects;

public class AbilityModifier {
    private final String name;

    public AbilityModifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AbilityModifier modifier)) return false;
        return Objects.equals(name, modifier.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
