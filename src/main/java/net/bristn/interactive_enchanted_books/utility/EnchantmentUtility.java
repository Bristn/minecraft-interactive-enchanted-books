package net.bristn.interactive_enchanted_books.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.resources.EnchantmentData;
import net.bristn.interactive_enchanted_books.resources.loader.EnchantmentDataLoader;
import net.bristn.interactive_enchanted_books.utility.wrappers.EnchantmentWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
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
    private static final Logger LOGGER = CommonModInitializer.LOGGER;

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

        // If the custom enchantment has not been added to the tooltip_order.json file,
        // it wont be added by the previous loop. In this case append the enchantment to
        // the end of the list. This might not match the books order, but ensures that
        // the page for the enchantment is properly shown
        for (var itemEnchantEntry : itemEnchants.entrySet()) {
            var isOrderedEnchant = false;

            for (var wrapper : enchantments) {
                if (wrapper.holder() == itemEnchantEntry.getKey()) {
                    isOrderedEnchant = true;
                    break;
                }
            }

            if (isOrderedEnchant == false) {
                enchantments.add(new EnchantmentWrapper(itemEnchantEntry.getIntValue(), itemEnchantEntry.getKey()));
            }
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
    public static EnchantmentData getParticleForEnchantment(Holder<Enchantment> holder) {
        var enchantment = holder.value();

        try {
            var enchantmentId = EnchantmentUtility.getEnchantmentIdentifier(holder);
            if (enchantmentId == null) {
                LOGGER.warn("EnchantmentUtility: Unable to get the id key of enchantment {}", enchantment.toString());
                return null;
            }

            var enchantmentParticles = EnchantmentDataLoader.getMap();
            if (enchantmentParticles.containsKey(enchantmentId) == false) {
                LOGGER.warn("EnchantmentUtility: Enchantment {} is not registered in the json", enchantmentId.toString());
                return null;
            }

            return enchantmentParticles.get(enchantmentId);
        } catch (Exception e) {
            LOGGER.error("EnchantmentUtility: An error occurred getting the particle for {}", enchantment.toString());
            e.printStackTrace();
            return null;
        }
    }

    public static Identifier getEnchantmentIdentifier(Holder<Enchantment> holder) {
        var resourceKey = holder.unwrapKey();
        if (resourceKey.isEmpty()) {
            return null;
        }

        return resourceKey.get().identifier();
    }
}
