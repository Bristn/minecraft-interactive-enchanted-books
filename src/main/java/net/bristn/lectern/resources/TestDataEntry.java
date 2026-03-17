package net.bristn.lectern.resources;

import java.util.HashSet;

import org.slf4j.Logger;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.resources.loader.ItemTagTextureLoader;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class TestDataEntry {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;
    private static final String FALLBACK_DESCRIPTION = "enchantment.lectern-enchanted-books.no-description";

    /**
     * Determines the localized enchantment description by using the key of the
     * title translation and appending a prefix. <br>
     * The method uses 3 steps to determine the translation. The first successful
     * step is returned <br>
     * 1. "title_key.desc.level-enchantmentLevel": The translation for the exact
     * enchantment and level (Allows listing details for each level) <br>
     * <b> Example: enchantment.minecraft.blast_protection.desc.level-4</b> <br>
     * 2. "title_key.desc"; The general translation for this enchantment. Has the
     * same format as <b>Enchantment Descriptions</b> <br>
     * <b>Example: enchantment.minecraft.blast_protection.desc</b> <br>
     * 3. A fallback translation that shows the user which translation keys need to
     * be implemented
     * 
     * @param enchantment
     * @param enchantmentLevel
     * @return
     */
    private String getEnchantmentDescription(Enchantment enchantment, int enchantmentLevel) {
        var descriptionKey = "n/a";
        var descriptionLevelKey = "n/a";

        // Try to read the translation key from the description contents
        var keyContent = enchantment.description().getContents();
        if (keyContent instanceof TranslatableContents translatable) {
            descriptionLevelKey = translatable.getKey() + ".desc.level-" + enchantmentLevel;
            var levelTranslation = Component.translatable(descriptionLevelKey).getString();

            // If there is a translation for the specific level, use it immediately
            if (descriptionLevelKey.equals(levelTranslation) == false) {
                return levelTranslation;
            }

            // Otherwise check if there is a general enchantment description
            descriptionKey = translatable.getKey() + ".desc";
            var translation = Component.translatable(descriptionKey).getString();
            if (descriptionKey.equals(translation) == false) {
                return translation;
            }
        }

        // If no translation has been found, use a fallback translation that is shipped
        // with the mod. Uses the other keys to inform the user which keys can be
        // implemented
        return Component.translatable(FALLBACK_DESCRIPTION, descriptionKey, descriptionLevelKey).getString();
    }

    public static void getSupportedItemTextures(HolderSet<Item> supportedItems) {
        var possibleTags = ItemTagTextureLoader.getMap();
        var usedTags = new HashSet<TagKey<Item>>();

        for (var holder : supportedItems) {
            var item = holder.value();
            var itemStack = item.getDefaultInstance();
            var itemTags = itemStack.tags();

            // Save the tags of the item that have a unique texture in a separate map
            var hasValidTag = false;
            for (var tag : itemTags.toList()) {
                if (possibleTags.containsKey(tag)) {
                    usedTags.add(tag);
                    hasValidTag = true;
                }
            }

            // If the item has no valid tag (no texture), show this to the user
            if (hasValidTag == false) {
                // TODO: Implement: Show in UI
                LOGGER.warn("Unable to get icon for item " + itemStack.getItemName().getString());
            }
        }

        LOGGER.info("----------------------- Loading texture for: " + usedTags.toString());
    }
}
