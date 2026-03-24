package net.justmili.servertweaks.mechanics.abilities;

import net.justmili.servertweaks.mechanics.abilities.registry.Ability;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilityRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Set;

public final class FoodCategories {

    public static final Set<Item> MEAT = Set.of(
        Items.PORKCHOP, Items.COOKED_PORKCHOP,
        Items.BEEF, Items.COOKED_BEEF,
        Items.MUTTON, Items.COOKED_MUTTON,
        Items.CHICKEN, Items.COOKED_CHICKEN,
        Items.COD, Items.COOKED_COD,
        Items.SALMON, Items.COOKED_SALMON,
        Items.TROPICAL_FISH,
        Items.PUFFERFISH,
        Items.RABBIT, Items.COOKED_RABBIT,
        Items.RABBIT_STEW,
        Items.ROTTEN_FLESH
    );

    public static final Set<Item> VEGETARIAN = Set.of(
        Items.BREAD,
        Items.CARROT,
        Items.BAKED_POTATO,
        Items.POTATO,
        Items.POISONOUS_POTATO,
        Items.BEETROOT,
        Items.BEETROOT_SOUP,
        Items.MELON_SLICE,
        Items.SWEET_BERRIES,
        Items.GLOW_BERRIES,
        Items.CHORUS_FRUIT,
        Items.MUSHROOM_STEW,
        Items.SUSPICIOUS_STEW,
        Items.DRIED_KELP,
        Items.APPLE,
        Items.COOKIE,
        Items.PUMPKIN_PIE,
        Items.CAKE,
        Items.HONEY_BOTTLE,
        Items.MILK_BUCKET
    );

    public static final Set<Item> SWEETS = Set.of(
        Items.SWEET_BERRIES,
        Items.GLOW_BERRIES,
        Items.COOKIE,
        Items.CAKE,
        Items.HONEY_BOTTLE,
        Items.MELON_SLICE,
        Items.PUMPKIN_PIE,
        Items.APPLE,
        Items.GOLDEN_APPLE,
        Items.ENCHANTED_GOLDEN_APPLE
    );

    public static final Set<Item> GOLDEN_FOODS = Set.of(
        Items.GOLDEN_APPLE,
        Items.ENCHANTED_GOLDEN_APPLE,
        Items.GOLDEN_CARROT
    );

    public static boolean canEat(Set<Ability> abilities, Set<AbilityModifiers> modifiers, Item item) {
        boolean hasDiet = abilities.contains(AbilityRegistry.CARNIVORE)
            || abilities.contains(AbilityRegistry.VEGETARIAN)
            || abilities.contains(AbilityRegistry.ONLY_EATS_SWEETS);
        if (!hasDiet) return true;

        if (modifiers.contains(AbilityModifiers.ADD_GOLD_FOODS_TO_DIET) && GOLDEN_FOODS.contains(item)) return true;

        if (abilities.contains(AbilityRegistry.CARNIVORE)) return MEAT.contains(item);
        if (abilities.contains(AbilityRegistry.VEGETARIAN)) return VEGETARIAN.contains(item);
        if (abilities.contains(AbilityRegistry.ONLY_EATS_SWEETS)) return SWEETS.contains(item);

        return true;
    }
}