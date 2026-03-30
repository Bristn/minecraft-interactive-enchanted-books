package net.bristn.lectern.tag;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {

    public static final TagKey<Item> BOWS = registerTag("bows");
    public static final TagKey<Item> CROSSBOWS = registerTag("crossbows");
    public static final TagKey<Item> FISHING_RODS = registerTag("fishing_rods");
    public static final TagKey<Item> ELYTRAS = registerTag("elytras");
    public static final TagKey<Item> SHIELDS = registerTag("shields");
    public static final TagKey<Item> TRIDENTS = registerTag("tridents");
    public static final TagKey<Item> MACES = registerTag("maces");

    public static final TagKey<Item> IGNITERS = registerTag("igniters");
    public static final TagKey<Item> SHEARS = registerTag("shears");
    public static final TagKey<Item> BRUSHES = registerTag("brushes");
    public static final TagKey<Item> SKULLS = registerTag("skulls");

    public static void registerModItemTags() {
        LecternEnchantedBooks.LOGGER.info("Register ModItemTags for" + LecternEnchantedBooks.MOD_ID);
    }

    private static TagKey<Item> registerTag(String name) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        return TagKey.create(Registries.ITEM, identifier);
    }
}
