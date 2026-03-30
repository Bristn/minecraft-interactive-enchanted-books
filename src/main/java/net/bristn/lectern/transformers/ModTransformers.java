package net.bristn.lectern.transformers;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ModTransformers {
    public static void registerModTransformers() {
        LecternEnchantedBooks.LOGGER.info("Register ModTransformers for" + LecternEnchantedBooks.MOD_ID);
    }

    public static final SimpleValueTransformer NONE = register("none", value -> {
        return twoDecimals(value);
    });

    public static final SimpleValueTransformer PERCENTAGE = register("percentage", value -> {
        return twoDecimals(value * 100f);
    });

    public static final SimpleValueTransformer LOYALTY_ACCELERATION = register("loyalty_acceleration", value -> {
        return twoDecimals(value * 0.05f);
    });

    /**
     * CombatRules -> getDamageAfterMagicAbsorb. A value of 25 would be 100%
     * reduction
     */
    public static final SimpleValueTransformer PROTECTION_PERCENTAGE = register("protection_percentage", value -> {
        return twoDecimals(value / 25f * 100f);
    });

    /**
     * Determines the fortune multiplier from the enchantment level
     */
    public static final SimpleValueTransformer FORTUNE_FROM_LEVEL = register("fortune_from_level", value -> {
        return twoDecimals(1f / (value + 2f) + (value + 1f) / 2);
    });

    private static SimpleValueTransformer register(String name, SimpleValueTransformerImpl transformer) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        return Registry.register(SimpleValueTransformer.REGISTRY, identifier, new SimpleValueTransformer(transformer));
    }

    private static float twoDecimals(float value) {
        return Math.round(value * 100f) / 100f;
    }
}