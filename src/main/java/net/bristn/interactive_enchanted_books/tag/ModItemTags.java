package net.bristn.interactive_enchanted_books.tag;

import java.util.List;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> BOWS = registerTag("bows");
    public static final TagKey<Item> CROSSBOWS = registerTag("crossbows");
    public static final TagKey<Item> ELYTRAS = registerTag("elytras");
    public static final TagKey<Item> SHIELDS = registerTag("shields");
    public static final TagKey<Item> TRIDENTS = registerTag("tridents");
    public static final TagKey<Item> MACES = registerTag("maces");

    public static final TagKey<Item> SKULLS = registerTag("skulls");
    public static final TagKey<Item> MISC = registerTag("misc");

    public static final List<TagKey<Item>> ALL_TAG_KEYS = List.of(BOWS, CROSSBOWS, ELYTRAS, SHIELDS, TRIDENTS, MACES, SKULLS,
            MISC);

    public static void registerModItemTags() {
        CommonModInitializer.LOGGER.info("Register ModItemTags for" + CommonModInitializer.MOD_ID);
    }

    private static TagKey<Item> registerTag(String name) {
        var identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
        return TagKey.create(Registries.ITEM, identifier);
    }
}
