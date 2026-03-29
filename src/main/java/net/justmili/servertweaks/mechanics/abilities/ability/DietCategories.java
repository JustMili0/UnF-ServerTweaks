package net.justmili.servertweaks.mechanics.abilities.ability;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class DietCategories {
    public static final Set<Item> CARNIVORE = Set.of(
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

    public static final Set<Item> SWEET = Set.of(
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

    public static final Set<Block> GRASSY = Set.of(
        Blocks.SHORT_GRASS,
        Blocks.TALL_GRASS,
        Blocks.SHORT_DRY_GRASS,
        Blocks.TALL_DRY_GRASS,
        Blocks.BUSH,
        Blocks.FIREFLY_BUSH
    );
}
