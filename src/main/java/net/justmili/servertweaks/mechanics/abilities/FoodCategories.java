package net.justmili.servertweaks.mechanics.abilities;

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

    // Plant-based foods + milk + honey (vegetarian-friendly)
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

    // Returns true if the player with these abilities is allowed to eat this item
    public static boolean canEat(Set<Ability> abilities, Item item) {
        // If no diet ability is present, no restrictions
        boolean hasDiet = abilities.contains(Ability.CARNIVORE)
            || abilities.contains(Ability.VEGETARIAN)
            || abilities.contains(Ability.ONLY_EATS_SWEETS);
        if (!hasDiet) return true;

        // Golden modifier adds golden foods to the allowed list regardless of diet
        if (abilities.contains(Ability.MODIFIER_EDIBLE_GOLDEN_FOODS) && GOLDEN_FOODS.contains(item)) return true;

        if (abilities.contains(Ability.CARNIVORE)) return MEAT.contains(item);
        if (abilities.contains(Ability.VEGETARIAN)) return VEGETARIAN.contains(item);
        if (abilities.contains(Ability.ONLY_EATS_SWEETS)) return SWEETS.contains(item);

        return true;
    }
}