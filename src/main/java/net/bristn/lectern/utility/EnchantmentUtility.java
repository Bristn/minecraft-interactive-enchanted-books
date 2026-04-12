package net.bristn.lectern.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.resources.EnchantmentData;
import net.bristn.lectern.resources.loader.EnchantmentDataLoader;
import net.bristn.lectern.utility.wrappers.EnchantmentWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class EnchantmentUtility {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    /**
     * Utility function to get the enchantments of an item in the correct ordering
     */
    public static List<EnchantmentWrapper> getSortedEnchantments(ItemStack book, Level level) {

        // Uses snippet of "addToTooltip" to properly order the enchantments. Using the
        // entrySet results in alphabetical ordering
        var itemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(book);
        var registries = Item.TooltipContext.of(level).registries();
        var order = getTagOrEmpty(registries, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);
        var enchantments = new ArrayList<EnchantmentWrapper>();

        for (var holder : order) {
            int enchantmentLevel = itemEnchants.getLevel(holder);
            if (enchantmentLevel <= 0) {
                continue;
            }

            enchantments.add(new EnchantmentWrapper(enchantmentLevel, holder));
        }

        return enchantments;
    }

    private static <T> HolderSet<T> getTagOrEmpty(HolderLookup.Provider registries, ResourceKey<Registry<T>> registry,
            TagKey<T> tag) {
        if (registries != null) {
            Optional<HolderSet.Named<T>> maybeOrder = registries.lookupOrThrow(registry).get(tag);
            if (maybeOrder.isPresent()) {
                return (HolderSet<T>) maybeOrder.get();
            }
        }

        return HolderSet.empty();
    }

    /**
     * Determines the particle from the enchantment. Tries to read the
     * enchantment_particle.json. If any error occurs, the default enchantment
     * particle is returned
     */
    public static EnchantmentData getParticleForEnchantment(Enchantment enchantment) {
        try {
            var enchantmentKey = EnchantmentUtility.getEnchantmentIdentifier(enchantment);
            if (enchantmentKey == null) {
                LOGGER.info("EnchantmentUtility: Unable to get the id key of enchantment {}", enchantment.toString());
                return null;
            }

            var enchantmentId = Identifier.parse(enchantmentKey);
            var enchantmentParticles = EnchantmentDataLoader.getMap();
            if (enchantmentParticles.containsKey(enchantmentId) == false) {
                LOGGER.info("EnchantmentUtility: Enchantment {} is not registered in the json", enchantment.toString());
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
