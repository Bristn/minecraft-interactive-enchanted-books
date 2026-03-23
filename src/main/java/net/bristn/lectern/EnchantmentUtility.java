package net.bristn.lectern;

import org.slf4j.Logger;

import net.bristn.lectern.resources.EnchantmentDataEntry;
import net.bristn.lectern.resources.loader.EnchantmentDataLoader;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentUtility {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    /**
     * Determines the particle from the enchantment. Tries to read the
     * enchantment_particle.json. If any error occurs, the default enchantment
     * particle is returned
     */
    public static EnchantmentDataEntry getParticleForEnchantment(Enchantment enchantment) {
        try {
            var enchantmentKey = EnchantmentUtility.getEnchantmentIdentifier(enchantment);
            if (enchantmentKey == null) {
                LOGGER.info("EnchantmentUtility: Unable to get the id key of enchantment {}", enchantment.toString());
                return null;
            }

            var enchantmentId = Identifier.parse(enchantmentKey);
            var enchantmentParticles = EnchantmentDataLoader.getMap();
            if (enchantmentParticles.containsKey(enchantmentId) == false) {
                LOGGER.info("EnchantmentUtility: Enchantment {} is not registered in th json", enchantment.toString());
                return null;
            }

            return enchantmentParticles.get(enchantmentId);
        } catch (Exception e) {
            LOGGER.info("EnchantmentUtility: An error occurred getting the particle for {}", enchantment.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static String getEnchantmentIdentifier(Enchantment enchantment) {
        var keyContent = enchantment.description().getContents();
        if (keyContent instanceof TranslatableContents translatable) {
            return translatable.getKey();
        }

        return null;
    }
}
