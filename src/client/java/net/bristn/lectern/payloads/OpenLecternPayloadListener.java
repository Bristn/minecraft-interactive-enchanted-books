package net.bristn.lectern.payloads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.resources.loader.ItemTagTextureLoader;
import net.bristn.lectern.screen.LecternEnchantedBookScreen;
import net.bristn.lectern.screen.data.LecternScreenData;
import net.bristn.lectern.screen.data.LecternScreenSupportedData;
import net.bristn.lectern.screen.data.LecternScreenSupportedIconData;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class OpenLecternPayloadListener {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;
    private static final String FALLBACK_DESCRIPTION = "enchantment.lectern-enchanted-books.no-description";

    /**
     * Use a custom payload to open the lectern screen with the correct item.
     * The tested code using mojangs default "createMenu" method did not sync the
     * book. Therefore use custom payload and open the menu on the client
     * 
     * @param payload
     * @param context
     */
    public static void handleOpenLecternPayload(OpenLecternPayload payload, Context context) {
        context.client().execute(() -> {
            var inventory = context.client().player.getInventory();
            var book = payload.book();
            var pages = getScreenPages(book);
            var menu = new LecternScreenHandler(0, inventory, book, pages);

            context.client().player.containerMenu = menu;
            var screen = new LecternEnchantedBookScreen(menu, inventory, Component.empty());
            context.client().setScreen(screen);
        });
    }

    private static List<LecternScreenData> getScreenPages(ItemStack book) {
        var pages = new ArrayList<LecternScreenData>();

        var itemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(book);
        for (var entry : itemEnchants.entrySet()) {
            var enchantment = entry.getKey().value();
            var enchantmentLevel = entry.getIntValue();

            // Determine the individual parts of each single enchantment page
            var descriptions = new ArrayList<Component>();
            descriptions.add(getEnchantmentDescription(enchantment, enchantmentLevel));
            var supported = getSupportedItemData(enchantment.getSupportedItems());

            var title = Component.literal("TODO: Title");
            var exclusive = new ArrayList<Component>();
            exclusive.add(Component.literal("TODO: Exclusive"));

            // Construct the page data
            var page = new LecternScreenData(supported, title, descriptions, exclusive);
            pages.add(page);
        }

        // TODO: Insert title page (All enchantments without exclusive set)

        return pages;
    }

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
    private static Component getEnchantmentDescription(Enchantment enchantment, int enchantmentLevel) {
        var descriptionKey = "n/a";
        var descriptionLevelKey = "n/a";

        // Try to read the translation key from the description contents
        var keyContent = enchantment.description().getContents();
        if (keyContent instanceof TranslatableContents translatable) {
            descriptionLevelKey = translatable.getKey() + ".desc.level-" + enchantmentLevel;
            var levelTranslation = Component.translatable(descriptionLevelKey).getString();

            // If there is a translation for the specific level, use it immediately
            if (descriptionLevelKey.equals(levelTranslation) == false) {
                return Component.translatable(descriptionLevelKey);
            }

            // Otherwise check if there is a general enchantment description
            descriptionKey = translatable.getKey() + ".desc";
            var translation = Component.translatable(descriptionKey).getString();
            if (descriptionKey.equals(translation) == false) {
                return Component.translatable(descriptionKey);
            }
        }

        // If no translation has been found, use a fallback translation that is shipped
        // with the mod. Uses the other keys to inform the user which keys can be
        // implemented
        return Component.translatable(FALLBACK_DESCRIPTION, descriptionKey, descriptionLevelKey);
    }

    /**
     * Uses the list of supported items to determine a list of textures. Uses the
     * loaded tags from the json file to get the relevant texture. If a supported
     * item does not have any tag that is contained in the json, the item is added
     * to the missing items list
     * 
     * @param supportedItems
     * @return
     */
    private static LecternScreenSupportedData getSupportedItemData(HolderSet<Item> supportedItems) {
        var supportedTags = ItemTagTextureLoader.getMap();

        // Iterate all supported items of the enchantment and populate the collections
        // The first map is used to keep track of the icon ordering that is determined
        // in the JSON loader. Format [order -> [tag -> entry]]
        var usedTagsByOrder = new HashMap<Integer, HashMap<TagKey<Item>, HashSet<Item>>>();
        var missingItemMap = new HashSet<Item>();
        for (var holder : supportedItems) {
            var item = holder.value();
            var itemStack = item.getDefaultInstance();
            var itemTags = itemStack.tags();

            // Save the tags of the item that have a unique texture in a separate map
            var hasValidTag = false;
            var tags = itemTags.toList();
            for (var tag : tags) {
                if (supportedTags.containsKey(tag)) {
                    var order = supportedTags.get(tag).order;
                    var orderMap = usedTagsByOrder.getOrDefault(order, new HashMap<>());
                    var itemMap = orderMap.getOrDefault(tag, new HashSet<>());
                    itemMap.add(item);

                    orderMap.put(tag, itemMap);
                    usedTagsByOrder.put(order, orderMap);
                    hasValidTag = true;
                }
            }

            // If the item has no valid tag (no texture), show this to the user
            if (hasValidTag == false) {
                missingItemMap.add(item);
                LOGGER.warn("Unable to get icon for {}  ", itemStack.getItemName().getString());

                for (var tag : tags) {
                    LOGGER.warn(tag.location().getNamespace());
                    LOGGER.warn(tag.location().toString());
                }

                // TODO: Manually add Tags to certain items? (These items don't have a unique
                // enough tag)
                // - Elytra
                // - Carrot on a stick, Warped fungus on a stick
            }
        }

        // Get the screen icons from the used tags
        var screenIcons = new ArrayList<LecternScreenSupportedIconData>();
        var orderedList = new ArrayList<Integer>(usedTagsByOrder.keySet());
        Collections.sort(orderedList);

        for (var order : orderedList) {
            var groupMap = usedTagsByOrder.get(order);

            for (var tagEntry : groupMap.entrySet()) {
                var tag = tagEntry.getKey();
                var items = new ArrayList<>(tagEntry.getValue());
                var iconTexture = supportedTags.get(tag).texture;

                // Determine the tooltip title (The name of the item tag)
                var tagKey = tag.getTranslationKey();
                var tagName = Component.translatable(tagKey).getString();

                // Sort the items to ensure tools & armor has the same ordering
                items.sort((a, b) -> a.toString().compareTo(b.toString()));
                screenIcons.add(new LecternScreenSupportedIconData(tagName, items, iconTexture));
            }
        }

        // Get the display names of the missing items
        var missingItemNames = new ArrayList<Component>();
        var missingItemItems = new ArrayList<Item>();
        var missingItemIcon = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "textures/gui/missing.png");
        for (var item : missingItemMap) {
            var itemName = item.toString();
            missingItemNames.add(Component.translatable(itemName));
            missingItemItems.add(item);
        }

        if (missingItemMap.size() != 0) {
            var tagName = Component.translatable("tag.item.lectern-enchanted-books.other").getString();
            missingItemItems.sort((a, b) -> a.toString().compareTo(b.toString()));
            screenIcons.add(new LecternScreenSupportedIconData(tagName, missingItemItems, missingItemIcon));
        }

        return new LecternScreenSupportedData(screenIcons);
    }

}
