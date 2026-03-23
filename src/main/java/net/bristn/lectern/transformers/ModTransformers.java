package net.bristn.lectern.transformers;

import org.slf4j.Logger;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class ModTransformers {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    public static void registerModTransformers() {
        LOGGER.info("Registering mod transformers");
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

    private static SimpleValueTransformer register(String name, SimpleValueTransformerImpl transformer) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        return Registry.register(SimpleValueTransformer.REGISTRY, identifier, new SimpleValueTransformer(transformer));
    }

    private static float twoDecimals(float value) {
        return Math.round(value * 100f) / 100f;
    }
}