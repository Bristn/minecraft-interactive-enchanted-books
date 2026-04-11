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

    public static final Function<Float, Float> FORTUNE_LUCK = register("fortune_luck", value -> {
        return twoDecimals(((1f / (value + 2f) + (value + 1f) / 2) - 1f) * 100f);
    });

    public static final Function<Float, Float> KNOCKBACK_DISTANCE = register("knockback_distance", value -> {
        return twoDecimals(value * 2.586f);
    });

    public static final Function<Float, Float> LOYALTY_SPEED = register("loyalty_speed", value -> {
        return twoDecimals(value * 16.67f);
    });

    public static final Function<Float, Float> LUCK_OF_THE_SEA_TREASURE = register("luck_of_the_sea_treasure", value -> {
        return twoDecimals(value * 2.1f);
    });

    public static final Function<Float, Float> LUCK_OF_THE_SEA_JUNK = register("luck_of_the_sea_junk", value -> {
        return twoDecimals(value * 1.96f);
    });

    public static final Function<Float, Float> POWER_DAMAGE = register("power_damage", value -> {
        return twoDecimals(25f * (value + 1f));
    });

    public static final Function<Float, Float> PUNCH_DISTANCE = register("punch_distance", value -> {
        return twoDecimals(value * 3.3f);
    });

    public static final Function<Float, Float> QUICK_CHARGE_DURATION = register("quick_charge_duration", value -> {
        return twoDecimals(value * 0.25f);
    });

    public static final Function<Float, Float> RESPIRATION_TIME = register("respiration_time", value -> {
        return twoDecimals(value * 15f);
    });

    public static final Function<Float, Float> RESPIRATION_CHANCE = register("respiration_chance", value -> {
        return twoDecimals((value / (value + 1f)) * 100f);
    });

    public static final Function<Float, Float> RIPTIDE_DISTANCE = register("riptide_distance", value -> {
        return twoDecimals((6f * value) + 3);
    });

    public static final Function<Float, Float> SOUL_SPEED = register("soul_speed", value -> {
        return twoDecimals(30.0f + (10.5f * value));
    });

    public static final Function<Float, Float> SWIFT_SNEAK = register("swift_sneak", value -> {
        return twoDecimals((value + 0.30f) * 100f);
    });

    public static final Function<Float, Float> THORNS_CHANCE = register("thorns_chance", value -> {
        return twoDecimals(value * 15f);
    });

    public static final Function<Float, Float> WIND_BURST_DISTANCE = register("wind_burst_distance", value -> {
        return twoDecimals(value * 8f);
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