package net.bristn.lectern.transformers;

import java.util.function.Function;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

/**
 * The different transforms can be used in the enchantment json to format
 * LevelBasedValues into human readable values
 */
public class ModTransformers {
    public static void registerModTransformers() {
        LecternEnchantedBooks.LOGGER.info("Register ModTransformers for" + LecternEnchantedBooks.MOD_ID);
    }

    // ! General transformers

    public static final Function<Float, Float> NONE = register("none", value -> {
        return twoDecimals(value);
    });

    public static final Function<Float, Float> PERCENTAGE = register("percentage", value -> {
        return Math.abs(twoDecimals(value * 100f));
    });

    public static final Function<Float, Float> DAMAGE_TO_HEARTS = register("damage_to_hearts", value -> {
        return twoDecimals(value / 2f);
    });

    public static final Function<Float, Float> TICKS_TO_SECONDS = register("ticks_to_seconds", value -> {
        return twoDecimals(value * 20f);
    });

    // Formulas taken from: https://minecraft.wiki/w/Enchantment

    // ! Enchantment specific transformers

    // Formula from code (max of 80% reduction possible)
    public static final Function<Float, Float> PROTECTION_PERCENTAGE = register("protection_percentage", value -> {
        return twoDecimals(Math.min(value / 25f, 0.8f) * 100f);
    });

    public static final Function<Float, Float> FORTUNE_LUCK = register("fortune_luck", level -> {
        return twoDecimals(((1f / (level + 2f) + (level + 1f) / 2) - 1f) * 100f);
    });

    public static final Function<Float, Float> KNOCKBACK_DISTANCE = register("knockback_distance", level -> {
        return twoDecimals(level * 2.586f);
    });

    public static final Function<Float, Float> LOYALTY_SPEED = register("loyalty_speed", level -> {
        return twoDecimals(level * 16.67f);
    });

    public static final Function<Float, Float> LUCK_OF_THE_SEA_TREASURE = register("luck_of_the_sea_treasure", level -> {
        return twoDecimals(level * 2.1f);
    });

    public static final Function<Float, Float> LUCK_OF_THE_SEA_JUNK = register("luck_of_the_sea_junk", value -> {
        return twoDecimals(value * 1.96f);
    });

    public static final Function<Float, Float> POWER_DAMAGE = register("power_damage", level -> {
        return twoDecimals(25f * (level + 1f));
    });

    public static final Function<Float, Float> PUNCH_DISTANCE = register("punch_distance", level -> {
        return twoDecimals(level * 3.3f);
    });

    public static final Function<Float, Float> QUICK_CHARGE_DURATION = register("quick_charge_duration", level -> {
        return twoDecimals(level * 0.25f);
    });

    public static final Function<Float, Float> RESPIRATION_TIME = register("respiration_time", level -> {
        return twoDecimals(level * 15f);
    });

    public static final Function<Float, Float> RESPIRATION_CHANCE = register("respiration_chance", level -> {
        return twoDecimals((level / (level + 1f)) * 100f);
    });

    public static final Function<Float, Float> RIPTIDE_DISTANCE = register("riptide_distance", level -> {
        return twoDecimals((6f * level) + 3);
    });

    public static final Function<Float, Float> SOUL_SPEED = register("soul_speed", level -> {
        return twoDecimals(30.0f + (10.5f * level));
    });

    public static final Function<Float, Float> SWIFT_SNEAK = register("swift_sneak", value -> {
        return twoDecimals((value + 0.30f) * 100f);
    });

    public static final Function<Float, Float> THORNS_CHANCE = register("thorns_chance", level -> {
        return twoDecimals(level * 15f);
    });

    public static final Function<Float, Float> WIND_BURST_DISTANCE = register("wind_burst_distance", level -> {
        return twoDecimals(level * 8f);
    });

    // ! Helper functions

    private static Function<Float, Float> register(String name, Function<Float, Float> transformer) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        var registry = ValueTransformer.getOrCreateRegistry();
        return Registry.register(registry, identifier, transformer);
    }

    private static float twoDecimals(float value) {
        return Math.round(value * 100f) / 100f;
    }
}