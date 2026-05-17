package net.bristn.interactive_enchanted_books.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.resources.loader.ItemTagTextureLoader;
import net.bristn.interactive_enchanted_books.screen.data.LecternScreenPageData;
import net.bristn.interactive_enchanted_books.screen.data.LecternScreenSupportedData;
import net.bristn.interactive_enchanted_books.screen.data.LecternScreenSupportedIconData;
import net.bristn.interactive_enchanted_books.tag.ModEnchantmentTags;
import net.bristn.interactive_enchanted_books.utility.wrappers.EnchantmentWrapper;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class LecternScreenPageUtility {
    /**
     * Utility function to determine the pages of the given enchanted book item. Iterates the different
     * enchantments and returns a separate page array entry for each enchantment
     */
    public static List<LecternScreenPageData> getScreenPages(ItemStack book, Level level) {
        var pages = new ArrayList<LecternScreenPageData>();

        var enchantments = EnchantmentUtility.getSortedEnchantments(book, level);
        var itemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(book);

        for (var wrapper : enchantments) {
            pages.add(getContentPage(wrapper.holder(), wrapper.enchantmentLevel()));
        }

        if (pages.size() > 1) {
            pages.add(0, getTitlePage(pages, enchantments, itemEnchants));
        }

        return pages;
    }

    /**
     * Get the content page. This includes title, description, exclusive set and support items
     */
    private static LecternScreenPageData getContentPage(Holder<Enchantment> holder, int enchantmentLevel) {
        var enchantment = holder.value();
        var leftHeaders = new ArrayList<MutableComponent>();
        var leftTexts = new ArrayList<MutableComponent>();

        var items = new HashSet<Item>();
        for (var item : enchantment.getSupportedItems()) {
            items.add(item.value());
        }

        // Determine the individual parts of each single enchantment page
        leftHeaders.add(Component.empty());
        leftTexts.add(Component.literal(getEnchantmentDescription(holder, enchantmentLevel)));
        var supported = getSupportedItemData(items);

        // Determine the title use for the page
        var title = Component.literal(enchantment.description().getString());
        if (enchantment.getMaxLevel() != 1) {
            title.append(" " + getRomanNumber(enchantmentLevel));
        }

        // Determine the list of mutually exclusive enchantments
        var exclusive = new ArrayList<String>();
        for (var exclusiveEnchant : enchantment.exclusiveSet()) {
            if (enchantment != exclusiveEnchant.value()) {
                exclusive.add(exclusiveEnchant.value().description().getString());
            }
        }

        if (exclusive.isEmpty() == false) {
            leftHeaders.add(Component.translatable("gui.interactive_enchanted_books.incompatible"));
            leftTexts.add(Component.literal(String.join(", ", exclusive)));
        }

        var signal = ModEnchantmentTags.getRedstoneSignal(holder);
        var redstoneSignal = Component.literal((signal == 0 ? "" : signal) + "");
        return new LecternScreenPageData(supported, title, leftHeaders, leftTexts, redstoneSignal);
    }

    /**
     * If there are multiple enchantments in the book, get a title page that shows the included
     * enchantments and all supported items. Does not include a list of exclusive sets
     */
    private static LecternScreenPageData getTitlePage(List<LecternScreenPageData> pages, List<EnchantmentWrapper> enchantments,
            ItemEnchantments itemEnchants) {

        var leftHeaders = new ArrayList<MutableComponent>();
        var leftTexts = new ArrayList<MutableComponent>();

        var title = Component.translatable("gui.interactive_enchanted_books.title");
        leftHeaders.add(Component.translatable("gui.interactive_enchanted_books.enchantments"));

        // Determine a set of all supported items from every enchantment
        var items = new HashSet<Item>();
        var enchantmentNames = new ArrayList<String>();
        var count = 1;
        for (var wrapper : enchantments) {
            var enchantment = wrapper.enchantment();
            var enchantmentLevel = wrapper.enchantmentLevel();
            for (var item : enchantment.getSupportedItems()) {
                items.add(item.value());
            }

            var line = count + ". " + enchantment.description().getString();
            if (enchantment.getMaxLevel() != 1) {
                line += " " + getRomanNumber(enchantmentLevel);
            }

            enchantmentNames.add(line);
            count++;
        }

        leftTexts.add(Component.literal(String.join("\n", enchantmentNames)));
        var supported = getSupportedItemData(items);

        var redstoneSignal = Component.literal("1");
        return new LecternScreenPageData(supported, title, leftHeaders, leftTexts, redstoneSignal);
    }

    /**
     * https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
     */
    private static String getRomanNumber(int number) {
        return "I".repeat(number).replace("IIIII", "V").replace("IIII", "IV").replace("VV", "X").replace("VIV", "IX")
                .replace("XXXXX", "L").replace("XXXX", "XL").replace("LL", "C").replace("LXL", "XC").replace("CCCCC", "D")
                .replace("CCCC", "CD").replace("DD", "M").replace("DCD", "CM");
    }

    /**
     * Determines the localized enchantment description by using the key of the title translation and
     * appending a prefix. <br>
     * The method uses 3 steps to determine the translation. The first successful step is returned <br>
     * 1. "title_key.desc.level-enchantmentLevel": The translation for the exact enchantment and level
     * (Allows listing details for each level) <br>
     * <b> Example: enchantment.minecraft.blast_protection.desc.level-4</b> <br>
     * 2. "title_key.desc"; The general translation for this enchantment. Has the same format as
     * <b>Enchantment Descriptions</b> <br>
     * <b>Example: enchantment.minecraft.blast_protection.desc</b> <br>
     * 3. A fallback translation that shows the user which translation keys need to be implemented
     */
    private static String getEnchantmentDescription(Holder<Enchantment> holder, int enchantmentLevel) {
        var descriptionKey = "n/a";
        var descriptionLevelKey = "n/a";
        var descriptionParamKey = "n/a";

        // Use the identifier of the enchantment to get the base translation key
        var identifier = EnchantmentUtility.getEnchantmentIdentifier(holder);
        if (identifier == null) {
            return Component.translatable("gui.interactive_enchanted_books.unable_to_get_identifier").getString();
        }

        var baseKey = "enchantment." + identifier.toString().replace(":", ".");

        // Get the named parameters for this translation
        var parameters = EnchantmentValueUtility.getTranslationParameters(holder, enchantmentLevel);

        // Try to read the translation key from the description contents
        descriptionLevelKey = baseKey + ".desc.level-" + enchantmentLevel;
        var levelTranslation = Component.translatable(descriptionLevelKey).getString();

        // If there is a translation for the specific level, use it immediately
        if (descriptionLevelKey.equals(levelTranslation) == false) {
            return levelTranslation;
        }

        // If there is no specific level, check if there is a generic level translation
        descriptionParamKey = baseKey + ".desc.level-x";
        var paramTranslation = Component.translatable(descriptionParamKey).getString();
        if (descriptionParamKey.equals(paramTranslation) == false) {

            // If there is a generic level description, apply the value formatters to get
            // the proper attribute values
            for (var parameter : parameters.entrySet()) {
                var placeholder = "{" + parameter.getKey() + "}";
                var value = parameter.getValue().toString();
                paramTranslation = paramTranslation.replace(placeholder, value);
            }

            return paramTranslation;
        }

        // Otherwise check if there is a general enchantment description
        descriptionKey = baseKey + ".desc";
        var translation = Component.translatable(descriptionKey).getString();
        if (descriptionKey.equals(translation) == false) {
            return translation;
        }

        // If no translation has been found, use a fallback translation that is shipped
        // with the mod. Uses the other keys to inform the user which keys can be
        // implemented
        var result = new ArrayList<String>();
        result.add(Component.translatable("gui.interactive_enchanted_books.no-description").getString());
        result.add(descriptionKey);
        result.add(descriptionLevelKey);
        return String.join("\n\n", result);
    }

    /**
     * Uses the list of supported items to determine a list of textures. Uses the loaded tags from the
     * json file to get the relevant texture. If a supported item does not have any tag that is
     * contained in the json, the item is added to the missing items list
     */
    private static LecternScreenSupportedData getSupportedItemData(HashSet<Item> supportedItems) {
        var supportedTags = ItemTagTextureLoader.getMap();
        var fabricItemTags = ClientTagUtility.getFabricItemTags();

        // Iterate all supported items of the enchantment and populate the collections
        // The first map is used to keep track of the icon ordering that is determined
        // in the JSON loader. Format [order -> [tag -> entry]]
        var usedTagsByOrder = new HashMap<Integer, HashMap<TagKey<Item>, HashSet<Item>>>();
        var missingItemMap = new HashSet<Item>();
        for (var item : supportedItems) {
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

            // The server might not send the custom item tags if the mod is not installed on
            // the server. Therefore also check the fabric client side item tags
            // ! To ensure client-only tags are recognized, the Fabric ClientTags are used
            for (var tag : fabricItemTags) {
                var hasTag = ClientTags.isInWithLocalFallback(tag, item);
                if (hasTag == false) {
                    continue;
                }

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

                var name = itemStack.getItemName().getString();
                var tagNames = new ArrayList<String>();
                for (var tag : tags) {
                    tagNames.add(tag.location().toString());
                }

                CommonModInitializer.LOGGER.warn("Unable to get supported item icon for {}. Tag list: {} ", name, tagNames);
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
        var missingItemIcon = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, "textures/gui/missing.png");
        for (var item : missingItemMap) {
            var itemName = item.toString();
            missingItemNames.add(Component.translatable(itemName));
            missingItemItems.add(item);
        }

        if (missingItemMap.size() != 0) {
            var tagName = Component.translatable("tag.item.interactive_enchanted_books.other").getString();
            missingItemItems.sort((a, b) -> a.toString().compareTo(b.toString()));
            screenIcons.add(new LecternScreenSupportedIconData(tagName, missingItemItems, missingItemIcon));
        }

        return new LecternScreenSupportedData(screenIcons);
    }
}
