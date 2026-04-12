package net.bristn.lectern;

import java.util.ArrayList;

import net.bristn.lectern.utility.EnchantmentValueUtility;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

@SuppressWarnings("UnstableApiUsage")
public class EnchantmentValueUtilityTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        var lookup = VanillaRegistries.createLookup();

        var registry = lookup.lookupOrThrow(Registries.ENCHANTMENT);
        var remaining = new ArrayList<ResourceKey<Enchantment>>();
        registry.listElements().forEach(holder -> {
            remaining.add(holder.key());
        });

        for (var key : remaining) {
            var enchantment = lookup.get(key).get().value();
            if (key == Enchantments.AQUA_AFFINITY) {
                continue;
            }

            if (key == Enchantments.BANE_OF_ARTHROPODS) {
                assertNamedParameter(enchantment, 1, "damage", 1.25F);
                assertNamedParameter(enchantment, 1, "minDuration", 1.5F);
                assertNamedParameter(enchantment, 1, "maxDuration", 1.5F);
                assertNamedParameter(enchantment, 2, "damage", 2.5F);
                assertNamedParameter(enchantment, 2, "minDuration", 1.5F);
                assertNamedParameter(enchantment, 2, "maxDuration", 2.0F);
                assertNamedParameter(enchantment, 3, "damage", 3.75F);
                assertNamedParameter(enchantment, 3, "minDuration", 1.5F);
                assertNamedParameter(enchantment, 3, "maxDuration", 2.5F);
                assertNamedParameter(enchantment, 4, "damage", 5.0F);
                assertNamedParameter(enchantment, 4, "minDuration", 1.5F);
                assertNamedParameter(enchantment, 4, "maxDuration", 3.0F);
                assertNamedParameter(enchantment, 5, "damage", 6.25F);
                assertNamedParameter(enchantment, 5, "minDuration", 1.5F);
                assertNamedParameter(enchantment, 5, "maxDuration", 3.5F);
                continue;
            }

            if (key == Enchantments.BLAST_PROTECTION) {
                assertNamedParameter(enchantment, 1, "knockback", 200.0F);
                assertNamedParameter(enchantment, 1, "reduction", 0.6F);
                assertNamedParameter(enchantment, 2, "knockback", 400.0F);
                assertNamedParameter(enchantment, 2, "reduction", 1.2F);
                assertNamedParameter(enchantment, 3, "knockback", 600.0F);
                assertNamedParameter(enchantment, 3, "reduction", 1.8F);
                assertNamedParameter(enchantment, 4, "knockback", 800.0F);
                assertNamedParameter(enchantment, 4, "reduction", 2.4F);

                continue;
            }

            if (key == Enchantments.BREACH) {
                assertNamedParameter(enchantment, 1, "reduction", 15.0F);
                assertNamedParameter(enchantment, 2, "reduction", 30.0F);
                assertNamedParameter(enchantment, 3, "reduction", 45.0F);
                assertNamedParameter(enchantment, 4, "reduction", 60.0F);
                continue;
            }

            if (key == Enchantments.CHANNELING) {
                continue;
            }

            if (key == Enchantments.BINDING_CURSE) {
                continue;
            }

            if (key == Enchantments.VANISHING_CURSE) {
                continue;
            }

            if (key == Enchantments.DENSITY) {
                assertNamedParameter(enchantment, 1, "damage", 0.25F);
                assertNamedParameter(enchantment, 2, "damage", 0.5F);
                assertNamedParameter(enchantment, 3, "damage", 0.75F);
                assertNamedParameter(enchantment, 4, "damage", 1.0F);
                assertNamedParameter(enchantment, 5, "damage", 1.25F);
                continue;
            }

            if (key == Enchantments.DEPTH_STRIDER) {
                assertNamedParameter(enchantment, 1, "speed", 33.33F);
                assertNamedParameter(enchantment, 2, "speed", 66.67F);
                assertNamedParameter(enchantment, 3, "speed", 100.0F);
                continue;
            }

            if (key == Enchantments.EFFICIENCY) {
                assertNamedParameter(enchantment, 1, "speed", 2.0F);
                assertNamedParameter(enchantment, 2, "speed", 5.0F);
                assertNamedParameter(enchantment, 3, "speed", 10.0F);
                assertNamedParameter(enchantment, 4, "speed", 17.0F);
                assertNamedParameter(enchantment, 5, "speed", 26.0F);
                continue;
            }

            if (key == Enchantments.FEATHER_FALLING) {
                assertNamedParameter(enchantment, 1, "reduction", 12.0F);
                assertNamedParameter(enchantment, 2, "reduction", 24.0F);
                assertNamedParameter(enchantment, 3, "reduction", 36.0F);
                assertNamedParameter(enchantment, 4, "reduction", 48.0F);
                continue;
            }

            if (key == Enchantments.FIRE_ASPECT) {
                assertNamedParameter(enchantment, 1, "duration", 4.0F);
                assertNamedParameter(enchantment, 2, "duration", 8.0F);
                continue;
            }

            if (key == Enchantments.FIRE_PROTECTION) {
                assertNamedParameter(enchantment, 1, "duration", 200.0F);
                assertNamedParameter(enchantment, 1, "damage", -0.6F);
                assertNamedParameter(enchantment, 2, "duration", 400.0F);
                assertNamedParameter(enchantment, 2, "damage", -1.2F);
                assertNamedParameter(enchantment, 3, "duration", 600.0F);
                assertNamedParameter(enchantment, 3, "damage", -1.8F);
                assertNamedParameter(enchantment, 4, "duration", 800.0F);
                assertNamedParameter(enchantment, 4, "damage", -2.4F);

                continue;
            }

            if (key == Enchantments.FLAME) {
                assertNamedParameter(enchantment, 1, "duration", 100.0F);
                continue;
            }

            if (key == Enchantments.FORTUNE) {
                assertNamedParameter(enchantment, 1, "increase", 33.33F);
                assertNamedParameter(enchantment, 2, "increase", 75.0F);
                assertNamedParameter(enchantment, 3, "increase", 120.0F);
                continue;
            }

            if (key == Enchantments.FROST_WALKER) {
                assertNamedParameter(enchantment, 1, "radius", 3.0F);
                assertNamedParameter(enchantment, 2, "radius", 4.0F);
                continue;
            }

            if (key == Enchantments.IMPALING) {
                assertNamedParameter(enchantment, 1, "damage", 1.25F);
                assertNamedParameter(enchantment, 2, "damage", 2.5F);
                assertNamedParameter(enchantment, 3, "damage", 3.75F);
                assertNamedParameter(enchantment, 4, "damage", 5.0F);
                assertNamedParameter(enchantment, 5, "damage", 6.25F);
                continue;
            }

            if (key == Enchantments.INFINITY) {
                continue;
            }

            if (key == Enchantments.KNOCKBACK) {
                assertNamedParameter(enchantment, 1, "distance", 2.59F);
                assertNamedParameter(enchantment, 2, "distance", 5.17F);
                continue;
            }

            if (key == Enchantments.LOOTING) {
                assertNamedParameter(enchantment, 1, "level", 1.0F);
                assertNamedParameter(enchantment, 1, "increase", 1.0F);
                assertNamedParameter(enchantment, 2, "level", 2.0F);
                assertNamedParameter(enchantment, 2, "increase", 2.0F);
                assertNamedParameter(enchantment, 3, "level", 3.0F);
                assertNamedParameter(enchantment, 3, "increase", 3.0F);

                continue;
            }

            if (key == Enchantments.LOYALTY) {
                assertNamedParameter(enchantment, 1, "speed", 16.67F);
                assertNamedParameter(enchantment, 2, "speed", 33.34F);
                assertNamedParameter(enchantment, 3, "speed", 50.01F);
                continue;
            }

            if (key == Enchantments.LUCK_OF_THE_SEA) {
                assertNamedParameter(enchantment, 1, "junk", 1.96F);
                assertNamedParameter(enchantment, 1, "treasure", 2.1F);
                assertNamedParameter(enchantment, 2, "junk", 3.92F);
                assertNamedParameter(enchantment, 2, "treasure", 4.2F);
                assertNamedParameter(enchantment, 3, "junk", 5.88F);
                assertNamedParameter(enchantment, 3, "treasure", 6.3F);
                continue;
            }

            if (key == Enchantments.LUNGE) {
                assertNamedParameter(enchantment, 1, "damage", 1.0F);
                assertNamedParameter(enchantment, 1, "distance", 9.16F);
                assertNamedParameter(enchantment, 1, "exhaustion", 4.0F);
                assertNamedParameter(enchantment, 2, "damage", 1.0F);
                assertNamedParameter(enchantment, 2, "distance", 18.32F);
                assertNamedParameter(enchantment, 2, "exhaustion", 8.0F);
                assertNamedParameter(enchantment, 3, "damage", 1.0F);
                assertNamedParameter(enchantment, 3, "distance", 27.48F);
                assertNamedParameter(enchantment, 3, "exhaustion", 12.0F);

                continue;
            }

            if (key == Enchantments.LURE) {
                assertNamedParameter(enchantment, 1, "duration", 5.0F);
                assertNamedParameter(enchantment, 2, "duration", 10.0F);
                assertNamedParameter(enchantment, 3, "duration", 15.0F);
                continue;
            }

            if (key == Enchantments.MENDING) {
                continue;
            }

            if (key == Enchantments.MULTISHOT) {
                assertNamedParameter(enchantment, 1, "quantity", 2.0F);
                assertNamedParameter(enchantment, 1, "spread", 10.0F);
                continue;
            }

            if (key == Enchantments.PIERCING) {
                assertNamedParameter(enchantment, 1, "quantity", 1.0F);
                assertNamedParameter(enchantment, 2, "quantity", 2.0F);
                assertNamedParameter(enchantment, 3, "quantity", 3.0F);
                assertNamedParameter(enchantment, 4, "quantity", 4.0F);
                continue;
            }

            if (key == Enchantments.POWER) {
                assertNamedParameter(enchantment, 1, "damage", 50.0F);
                assertNamedParameter(enchantment, 2, "damage", 75.0F);
                assertNamedParameter(enchantment, 3, "damage", 100.0F);
                assertNamedParameter(enchantment, 4, "damage", 125.0F);
                assertNamedParameter(enchantment, 5, "damage", 150.0F);
                continue;
            }

            if (key == Enchantments.PROJECTILE_PROTECTION) {
                assertNamedParameter(enchantment, 1, "reduction", 8.0F);
                assertNamedParameter(enchantment, 2, "reduction", 16.0F);
                assertNamedParameter(enchantment, 3, "reduction", 24.0F);
                assertNamedParameter(enchantment, 4, "reduction", 32.0F);
                continue;
            }

            if (key == Enchantments.PROTECTION) {
                assertNamedParameter(enchantment, 1, "reduction", 4.0F);
                assertNamedParameter(enchantment, 2, "reduction", 8.0F);
                assertNamedParameter(enchantment, 3, "reduction", 12.0F);
                assertNamedParameter(enchantment, 4, "reduction", 16.0F);
                continue;
            }

            if (key == Enchantments.PUNCH) {
                assertNamedParameter(enchantment, 1, "knockback", 3.3F);
                assertNamedParameter(enchantment, 2, "knockback", 6.6F);
                continue;
            }

            if (key == Enchantments.QUICK_CHARGE) {
                assertNamedParameter(enchantment, 1, "duration", 0.25F);
                assertNamedParameter(enchantment, 2, "duration", 0.5F);
                assertNamedParameter(enchantment, 3, "duration", 0.75F);
                continue;
            }

            if (key == Enchantments.RESPIRATION) {
                assertNamedParameter(enchantment, 1, "duration", 15.0F);
                assertNamedParameter(enchantment, 1, "chance", 50.0F);
                assertNamedParameter(enchantment, 2, "duration", 30.0F);
                assertNamedParameter(enchantment, 2, "chance", 66.67F);
                assertNamedParameter(enchantment, 3, "duration", 45.0F);
                assertNamedParameter(enchantment, 3, "chance", 75.0F);

                continue;
            }

            if (key == Enchantments.RIPTIDE) {
                assertNamedParameter(enchantment, 1, "distance", 9.0F);
                assertNamedParameter(enchantment, 2, "distance", 15.0F);
                assertNamedParameter(enchantment, 3, "distance", 21.0F);
                continue;
            }

            if (key == Enchantments.SHARPNESS) {
                assertNamedParameter(enchantment, 1, "damage", 0.5F);
                assertNamedParameter(enchantment, 2, "damage", 0.75F);
                assertNamedParameter(enchantment, 3, "damage", 1.0F);
                assertNamedParameter(enchantment, 4, "damage", 1.25F);
                assertNamedParameter(enchantment, 5, "damage", 1.5F);
                continue;
            }

            if (key == Enchantments.SILK_TOUCH) {
                continue;
            }

            if (key == Enchantments.SMITE) {
                assertNamedParameter(enchantment, 1, "damage", 1.25F);
                assertNamedParameter(enchantment, 2, "damage", 2.5F);
                assertNamedParameter(enchantment, 3, "damage", 3.75F);
                assertNamedParameter(enchantment, 4, "damage", 5.0F);
                assertNamedParameter(enchantment, 5, "damage", 6.25F);
                continue;
            }

            if (key == Enchantments.SOUL_SPEED) {
                assertNamedParameter(enchantment, 1, "speed", 40.5F);
                assertNamedParameter(enchantment, 2, "speed", 51.0F);
                assertNamedParameter(enchantment, 3, "speed", 61.5F);
                continue;
            }

            if (key == Enchantments.SWEEPING_EDGE) {
                assertNamedParameter(enchantment, 1, "increase", 50.0F);
                assertNamedParameter(enchantment, 2, "increase", 66.67F);
                assertNamedParameter(enchantment, 3, "increase", 75.0F);
                continue;
            }

            if (key == Enchantments.SWIFT_SNEAK) {
                assertNamedParameter(enchantment, 1, "increase", 45.0F);
                assertNamedParameter(enchantment, 2, "increase", 60.0F);
                assertNamedParameter(enchantment, 3, "increase", 75.0F);
                continue;
            }

            if (key == Enchantments.THORNS) {
                assertNamedParameter(enchantment, 1, "chance", 15.0F);
                assertNamedParameter(enchantment, 1, "maxDamage", 2.5F);
                assertNamedParameter(enchantment, 1, "minDamage", 1.0F);
                assertNamedParameter(enchantment, 2, "chance", 30.0F);
                assertNamedParameter(enchantment, 2, "maxDamage", 2.5F);
                assertNamedParameter(enchantment, 2, "minDamage", 1.0F);
                assertNamedParameter(enchantment, 3, "chance", 45.0F);
                assertNamedParameter(enchantment, 3, "maxDamage", 2.5F);
                assertNamedParameter(enchantment, 3, "minDamage", 1.0F);
                continue;
            }

            if (key == Enchantments.UNBREAKING) {
                assertNamedParameter(enchantment, 1, "chance", 50.0F);
                assertNamedParameter(enchantment, 2, "chance", 66.67F);
                assertNamedParameter(enchantment, 3, "chance", 75.0F);
                continue;
            }

            if (key == Enchantments.WIND_BURST) {
                assertNamedParameter(enchantment, 1, "distance", 8.0F);
                assertNamedParameter(enchantment, 2, "distance", 16.0F);
                assertNamedParameter(enchantment, 3, "distance", 24.0F);
                continue;
            }

            LecternEnchantedBooks.LOGGER.error("Missed parameter tests for: {}", key);
        }
    }

    private void assertNamedParameter(Enchantment enchantment, int level, String name, float expected) {
        var parameters = EnchantmentValueUtility.getTranslationParameters(enchantment, level);
        if (parameters.get(name) != expected)
            LecternEnchantedBooks.LOGGER.error("Wrong named parameter {}: {} level {}", name, enchantment.toString(), level);
    }
}
